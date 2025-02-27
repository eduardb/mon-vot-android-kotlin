package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import kotlinx.android.synthetic.main.fragment_question_details.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.QuestionDetailsAdapter
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.addOnLayoutChangeListenerForGalleryEffect
import ro.code4.monitorizarevot.helper.addOnScrollListenerForGalleryEffect
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel


class QuestionsDetailsFragment : BaseFragment<QuestionsDetailsViewModel>() {


    override val layout: Int
        get() = R.layout.fragment_question_details
    override val viewModel: QuestionsDetailsViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private lateinit var adapter: QuestionDetailsAdapter
    private var currentPosition: Int = 0
    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(mContext, HORIZONTAL, false)
    }

    companion object {
        val TAG = QuestionsDetailsFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.questions().observe(this, Observer {
            setData(it)
        })
        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((Constants.FORM))))
        list.layoutManager = layoutManager

        list.addOnScrollListenerForGalleryEffect()
        list.addOnLayoutChangeListenerForGalleryEffect()

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(list)
        nextQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition < adapter.itemCount - 1) {//todo check if you can remove this, in theory the button shouldn't be visible when currentPosition ==  adapter.itemCount - 1
                list.smoothScrollToPosition(currentPosition + 1)
            }
        }
        previousQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition > 0) { //todo check if you can remove this, in theory the button shouldn't be visible when currentPosition == 0
                list.smoothScrollToPosition(currentPosition - 1)
            }
        }

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var x = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (currentPosition > 0) {
                        viewModel.saveAnswer(adapter.getItem(currentPosition - x))
                    }
                    setVisibilityOnButtons()

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                x = if (dx > 0) 1 else -1
            }
        })

    }

    private fun setVisibilityOnButtons() {
        when (currentPosition) {
            0 -> previousQuestionBtn.visibility = View.GONE
            adapter.itemCount - 1 -> nextQuestionBtn.visibility = View.GONE
            else -> {
                previousQuestionBtn.visibility = View.VISIBLE
                nextQuestionBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun setData(items: ArrayList<QuestionWithAnswers>) {
        if (!::adapter.isInitialized) {
            adapter = QuestionDetailsAdapter(mContext, items)
            list.adapter = adapter
            currentPosition = items.indexOfFirst {
                it.question.id == Parcels.unwrap<Question>(arguments?.getParcelable((Constants.QUESTION))).id
            }
            layoutManager.scrollToPosition(currentPosition)
            setVisibilityOnButtons()
        } else {
            adapter.refreshData(items)
        }
    }

    override fun onPause() {
        viewModel.saveAnswer(adapter.getItem(currentPosition))
        viewModel.syncData()
        super.onPause()

    }
}