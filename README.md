# Android-ToggleButton

![Alt text](https://github.com/hahaoop/Android-ToggleButton/raw/master/Screenshots/s1.gif)
![Alt text](https://github.com/hahaoop/Android-ToggleButton/raw/master/Screenshots/s2.gif)
#### Config in xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:toggle="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="com.hahaoop.togglebutton.MainActivity">

    <com.hahaoop.togglebutton.ToggleButton
        android:id="@+id/test"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <com.hahaoop.togglebutton.ToggleButton
        android:id="@+id/test1"
        android:layout_width="wrap_content"
        toggle:toggleState="open"
        android:layout_height="wrap_content" />
    <com.hahaoop.togglebutton.ToggleButton
        android:layout_width="wrap_content"
        toggle:toggleOpenBackgroundColor="@color/colorAccent"
        toggle:toggleCloseBackgroundColor="@color/colorPrimaryDark"
        android:layout_height="wrap_content" />
</LinearLayout>
```
#### Other property
##### use isOpen() to get togglebutton's status
```
isOpen()
```