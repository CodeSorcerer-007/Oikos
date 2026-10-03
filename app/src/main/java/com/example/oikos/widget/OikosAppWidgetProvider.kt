package com.example.oikos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.data.db.OikosDatabaseHelper

class OikosAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, OikosAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_oikos_glance)

            // Query items from database
            val dbHelper = OikosDatabaseHelper(context)
            val lists = dbHelper.getAllLists()
            val activeList = lists.firstOrNull { it.isDefault } ?: lists.firstOrNull()
            val listId = activeList?.id ?: "default_weekly"
            val listTitle = activeList?.name ?: "Weekly Groceries"

            val allItems = dbHelper.getItemsForList(listId)
            val pendingItems = allItems.filter { !it.isCompleted }

            views.setTextViewText(R.id.widget_list_title, listTitle)
            views.setTextViewText(
                R.id.widget_count_badge,
                if (pendingItems.isEmpty()) "Done" else "${pendingItems.size} left"
            )

            if (pendingItems.isEmpty()) {
                views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE)
                views.setViewVisibility(R.id.widget_item_1, View.GONE)
                views.setViewVisibility(R.id.widget_item_2, View.GONE)
                views.setViewVisibility(R.id.widget_item_3, View.GONE)
            } else {
                views.setViewVisibility(R.id.widget_empty_text, View.GONE)

                // Item 1
                if (pendingItems.isNotEmpty()) {
                    views.setViewVisibility(R.id.widget_item_1, View.VISIBLE)
                    views.setTextViewText(
                        R.id.widget_item_1,
                        "• ${pendingItems[0].name} (${pendingItems[0].formattedQuantity})"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_1, View.GONE)
                }

                // Item 2
                if (pendingItems.size > 1) {
                    views.setViewVisibility(R.id.widget_item_2, View.VISIBLE)
                    views.setTextViewText(
                        R.id.widget_item_2,
                        "• ${pendingItems[1].name} (${pendingItems[1].formattedQuantity})"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_2, View.GONE)
                }

                // Item 3
                if (pendingItems.size > 2) {
                    views.setViewVisibility(R.id.widget_item_3, View.VISIBLE)
                    val extra = if (pendingItems.size > 3) " (+${pendingItems.size - 3} more)" else ""
                    views.setTextViewText(
                        R.id.widget_item_3,
                        "• ${pendingItems[2].name} (${pendingItems[2].formattedQuantity})$extra"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_3, View.GONE)
                }
            }

            // Launch MainActivity when tapping the button or widget
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_btn_open, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
