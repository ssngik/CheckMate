package kr.co.company.capstone.util

import android.os.Bundle
import android.view.View
import kr.co.company.capstone.R

class FragmentUtil {
    fun actionDetailToInvite(view : View, goalId:Long){
        val bundle = Bundle()
        bundle.putLong("goalId", goalId)
        NavigationUtil.navigateTo(view, R.id.action_goalDetailFragment_to_inviteUserFragment, bundle)
    }

    fun actionDetailToTimeLine(view: View, goalId: Long){
        val bundle = Bundle()
        bundle.putLong("goalId", goalId)
        NavigationUtil.navigateTo(view, R.id.action_goalDetailFragment_to_timeLineFragment, bundle)
    }

    fun actionDetailToDoMyGoal(view: View){
        NavigationUtil.navigateTo(view, R.id.action_goalDetailFragment_to_doMyGoalFragment)
    }

}