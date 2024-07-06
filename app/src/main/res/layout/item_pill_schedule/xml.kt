package layout.item_pill_schedule

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:orientation="vertical"
android:padding="16dp"
android:background="@drawable/item_background"
android:marginBottom="8dp">

<TextView
android:id="@+id/pillNameTextView"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="Pill Name"
android:textSize="18sp"
android:textStyle="bold" />

<TextView
android:id="@+id/pillTimeTextView"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="Time"
android:textSize="16sp" />
</LinearLayout>
