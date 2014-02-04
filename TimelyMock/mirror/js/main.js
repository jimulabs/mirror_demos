$("@id/pager").on("pageTransform",
    function(leftView, rightView, leftPosition, rightPosition) {
    });

$("@id/timeSwitch").on("checkedChanged",
    function(view, checked) {
        view.parent.$("@id/time").style = checked ?
            "@style/alarmOnTextStyle" : "@style/alarmOffTextStyle"
        view.parent.$("@id/cloud1").src = checked ?
            "@drawable/cloud_notifier_big_on" : "@drawable/cloud_notifier_big_off"
    });