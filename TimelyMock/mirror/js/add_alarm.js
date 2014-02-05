var timeBar = $('@id/timeBar')

var timeText = $('@id/time', timeBar)
var oldX = timeText.x

function getNewX(touchX) {
    var oneThird = timeBar.width/3 - timeText.width
    var twoThird = timeBar.width/3 * 2
    var touchedLeft = touchX < timeText.x + timeText.width/2
    return touchedLeft ? twoThird : oneThird
}

var lastY = null

timeBar.on('touch',
    function(view, event) {
        if (event.type == 'move') {
            var deltaY = lastY!=null ? event.y-lastY : 0
            deltaY = Math.min(50, Math.max(-50, deltaY))
            //log("deltaY="+deltaY+' event.y='+event.y+' lastY='+lastY)
            lastY = event.y
            timeBar.y += deltaY
        } else {
            var isUp = event.type == 'up';
            var newX = getNewX(event.x)
            var toX = isUp ? oldX : newX;
            var toScale = isUp ? 1 : 0.8;
            timeText.animate({
                properties: {
                    x: toX,
                    scale: toScale
                },
                interpolator: '@android:interpolator/decelerate_cubic',
                duration: 250
            });
            if (isUp) timeBar.animate('@animator/bounce_y')
            if (event.type=='down') lastY = null
        }
    });

