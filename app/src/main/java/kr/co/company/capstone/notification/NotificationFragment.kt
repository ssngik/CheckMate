package kr.co.company.capstone.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentNotificationBinding
import kr.co.company.capstone.dto.notification.NotificationResponse
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.util.adapter.NotificationsAdapter

class NotificationFragment : Fragment(), NotificationContract.NotificationView, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : NotificationContract.NotificationPresenter
    private lateinit var adapter : NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        presenter = NotificationPresenter(this, NotificationModel() )
        return binding.root
}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadNotifications()
        setupRecyclerView()
        setRefresh()
    }

    // 최초 알림 호출
    override fun fetchNotificationsRecyclerView(result: NotificationResponse) {
        adapter.initNotifications(result.notifications)
    }

    // 추가적인 알림
    override fun appendNotifications(result: NotificationResponse) {
        adapter.addNotifications(result.notifications)
    }

    private fun setupRecyclerView(){
        adapter = NotificationsAdapter(this, mutableListOf())
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notificationRecyclerView.adapter = adapter

        binding.notificationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val upTo = 4
                if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount-upTo){
                    // 추가 알림 호출
                    val cursorId = adapter.notifications.lastOrNull()?.notificationId
                    if (cursorId != null){
                        presenter.loadAdditionalNotifications(cursorId)
                    }
                }
            }
        })
    }

    override fun showErrorDialog(errorMessage : String) {
        ErrorDialogFragment.getErrorMessage(errorMessage).show(childFragmentManager, "error_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }

    override fun onRefresh() {
        presenter.loadNotifications()
        binding.refresh.isRefreshing=false
    }

    private fun setRefresh(){
        binding.refresh.setOnRefreshListener(this)
        binding.refresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.checkmate_color))
    }
}