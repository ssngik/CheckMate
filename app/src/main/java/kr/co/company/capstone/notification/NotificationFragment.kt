package kr.co.company.capstone.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.company.capstone.databinding.FragmentNotificationBinding
import kr.co.company.capstone.dto.notification.NotificationResponse
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.util.adapter.NotificationsAdapter

class NotificationFragment : Fragment(), NotificationContract.NotificationView {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : NotificationContract.NotificationPresenter

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
    }

    override fun fetchNotificationsRecyclerView(result: NotificationResponse) {
        val notifications = result.notifications
        val adapter =  NotificationsAdapter(this, notifications)
        val notificationRecyclerView = binding.notificationRecyclerView

        notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        notificationRecyclerView.adapter = adapter
    }

    override fun showErrorDialog(errorMessage : String) {
        ErrorDialogFragment.getErrorMessage(errorMessage).show(childFragmentManager, "error_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }
}