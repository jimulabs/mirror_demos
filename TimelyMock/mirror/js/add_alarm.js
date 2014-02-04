var timeBar = $('@id/timeBar')

var oldX = timeBar.x

timeBar.on('touch',
    function(view, event) {
        var isUp = event.type == 'up';
        var newX = timeBar.width - event.x;
        var toX = isUp ? oldX : newX;
        var toScale = isUp ? 1 : 0.8;
        view.$('@id/time').animate({
            properties: {
                x: toX,
                scale: toScale
            },
            interpolator: '@android:interpolator/bounce',
            duration: 100
        });
    });

/*
timeBar.on('drag',
    function(view, event) {
        view.y = event.y
    });
*/