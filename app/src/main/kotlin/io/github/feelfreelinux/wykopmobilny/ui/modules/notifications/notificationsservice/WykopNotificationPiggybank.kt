package io.github.feelfreelinux.wykopmobilny.ui.modules.notifications.notificationsservice

import android.service.notification.StatusBarNotification
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.annotation.RequiresApi
import android.text.TextUtils
import dagger.android.AndroidInjection
import io.github.feelfreelinux.wykopmobilny.utils.preferences.SettingsPreferences
import io.github.feelfreelinux.wykopmobilny.utils.preferences.SettingsPreferencesApi
import io.github.feelfreelinux.wykopmobilny.utils.printout
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
/***
 * `NotificationPiggyback` captures notifications from official wykop.pl application,
 * and runs singleshot notification job instead.
 */
class NotificationPiggyback : NotificationListenerService() {
    val settingsPreferencesApi by lazy { SettingsPreferences(applicationContext) }
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (settingsPreferencesApi.piggyBackPushNotifications) {
            val packageName = sbn.packageName
            if (!TextUtils.isEmpty(packageName) && packageName == "pl.wykop.droid" || packageName == "com.tylerr147.notisend") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cancelNotification(sbn.key)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    cancelNotification(packageName, sbn.tag, sbn.id)
                }
                WykopNotificationsJob.scheduleOnce()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
    }
}