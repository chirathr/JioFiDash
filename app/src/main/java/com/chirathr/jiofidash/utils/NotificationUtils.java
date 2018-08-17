package com.chirathr.jiofidash.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.chirathr.jiofidash.MainActivity;
import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.SplashScreen;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtils {

    private static final int BATTERY_REMINDER_NOTIFICATION_ID = 1;
    private static final String BATTERY_REMINDER_NOTIFICATION_CHANNEL_ID = "battery_low";

    private static final int BATTERY_REMINDER_PENDING_INTENT_ID = 2;

    // Return the pending intent required to start the activity when the user clicks the notification
    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                BATTERY_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    // Return the large icon required for creating a notification
    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();

        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground);
    }

    public static void remindUserBatteryLow(Context context, int batteryPercentage) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);


        // Oreo notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    BATTERY_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.battery_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            } else {
                return;
            }
        }

        // Build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, BATTERY_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_round_battery_alert_24px)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(String.format(context.getString(
                        R.string.battery_notification_title_format_string), batteryPercentage))
                .setContentText(context.getString(R.string.battery_notification_body_format_string))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.battery_notification_body_format_string)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        // Priority for devices other than oreo and below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(BATTERY_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static void cancelAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}
