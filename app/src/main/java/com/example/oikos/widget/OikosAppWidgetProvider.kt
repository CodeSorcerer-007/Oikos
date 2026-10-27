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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OikosAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, OikosAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            CoroutineScope(Dispatchers.IO).launch {
                for (widgetId in allWidgetIds) {
                    updateAppWidget(context, appWidgetManager, widgetId)
                }
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_oikos_glance)

            // Query items from singleton database on background thread
            val dbHelper = OikosDatabaseHelper.getInstance(context)
            val lists = dbHelper.getAllLists()
            val activeList = lists.firstOrNull { it.isDefault } ?: lists.firstOrNull()
            val listId = activeList?.id ?: "default_weekly"
            val listTitle = activeList?.name ?: "Weekly Groceries"

            val allItems = dbHelper.getItemsForList(listId)
            val pendingItems = allItems.filter { !it.isCompleted }

            val totalCount = allItems.size
            val completedCount = allItems.count { it.isCompleted }
            val progressPercent = if (totalCount > 0) ((completedCount * 100) / totalCount) else 0

            views.setProgressBar(R.id.widget_progress_bar, 100, progressPercent, false)
            views.setTextViewText(R.id.widget_list_title, "$listTitle · $completedCount/$totalCount")
            views.setTextViewText(
                R.id.widget_count_badge,
                if (pendingItems.isEmpty()) "All Done ✓" else "${pendingItems.size} left"
            )

            if (pendingItems.isEmpty()) {
                views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE)
                views.setViewVisibility(R.id.widget_item_1, View.GONE)
                views.setViewVisibility(R.id.widget_item_2, View.GONE)
                views.setViewVisibility(R.id.widget_item_3, View.GONE)
                views.setViewVisibility(R.id.widget_item_4, View.GONE)
                views.setViewVisibility(R.id.widget_item_5, View.GONE)
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
                    views.setTextViewText(
                        R.id.widget_item_3,
                        "• ${pendingItems[2].name} (${pendingItems[2].formattedQuantity})"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_3, View.GONE)
                }

                // Item 4
                if (pendingItems.size > 3) {
                    views.setViewVisibility(R.id.widget_item_4, View.VISIBLE)
                    views.setTextViewText(
                        R.id.widget_item_4,
                        "• ${pendingItems[3].name} (${pendingItems[3].formattedQuantity})"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_4, View.GONE)
                }

                // Item 5
                if (pendingItems.size > 4) {
                    views.setViewVisibility(R.id.widget_item_5, View.VISIBLE)
                    val extra = if (pendingItems.size > 5) " (+${pendingItems.size - 5} more)" else ""
                    views.setTextViewText(
                        R.id.widget_item_5,
                        "• ${pendingItems[4].name} (${pendingItems[4].formattedQuantity})$extra"
                    )
                } else {
                    views.setViewVisibility(R.id.widget_item_5, View.GONE)
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
