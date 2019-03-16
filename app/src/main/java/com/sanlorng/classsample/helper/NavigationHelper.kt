package com.sanlorng.classsample.helper

import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.sanlorng.classsample.R

object NavigationHelper {
    val navOptions = navOptions {
        anim {
            enter = R.anim.nav_default_enter_anim
            popEnter = R.anim.nav_default_pop_enter_anim
            exit = R.anim.nav_default_exit_anim
            popExit = R.anim.nav_default_pop_exit_anim
        }
    }
}

fun NavController.navigationDefaultAnim(resId: Int) {
    navigate(resId, null, NavigationHelper.navOptions)
}