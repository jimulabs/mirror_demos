var timeBar = $('@id/timeBar')

var timeText = $('@id/time', timeBar)

function playRipple(x, y) {
    var ripple = $('@id/ripple')
    ripple.alpha = 1
    ripple.scale = 1
    ripple.x = x - ripple.width/2
    ripple.y = y - ripple.height/2
    ripple.animate({
        properties: {
            scale: 4,
            alpha: 0
        },
        duration: 601
    });
}

function calcTimeTextSideX(touchX) {
    var oneThird = timeBar.width/3 - timeText.width
    var twoThird = timeBar.width/3 * 2
    var touchedLeft = touchX < timeText.x + timeText.width/2
    return touchedLeft ? twoThird : oneThird
}

var timeBarYOffset = 0
var timeBarParent = timeBar.parent
var timeBarYMargin = 100
var currentMinutes = 0

function setTime(ratio, stepInMinute) {
    ratio = Math.max(0, Math.min(1, ratio))
    timeBar.y = ratio * (timeBarParent.height - timeBarYMargin - timeBar.height) + timeBar.height
    var hm = ratio * 24
    var h = Math.floor(hm)
    var m = Math.round((hm - h)*60)
    if (stepInMinute) m = Math.floor(m/stepInMinute)*stepInMinute
    var zeropadding = function(x) { return x<10 ? '0'+x : x }
    timeText.text = zeropadding(h) + ':' + zeropadding(m)
    currentMinutes = h*60 + m
}

function setTimeInMinutes(minutes) {
    setTime(minutes / (24*60))
}

setTimeInMinutes(10*60+30)

var hitTimeBarWhenDown = false
timeBarParent.on('touch',
    function(view, event) {
        if (event.type == 'move' && hitTimeBarWhenDown) {
            var ratio = (event.y- timeBarYMargin) / (timeBarParent.height - timeBarYMargin)
            setTime(ratio, 30)
        } else {
            var isUp = event.type == 'up'
            var isDown = event.type == 'down'
            var hitTimeBar = timeBar.y<=event.y && event.y<=timeBar.y+timeBar.height
            if (isDown) hitTimeBarWhenDown = hitTimeBar

            if (hitTimeBarWhenDown) {
                var sideX = calcTimeTextSideX(event.x)
                var centerX = (timeBar.width - timeText.width)/2
                timeText.animate({
                    properties: {
                        x: isUp ? centerX : sideX,
                        scale: isUp ? 1 : 0.8
                    },
                    interpolator: '@android:interpolator/decelerate_cubic',
                    duration: 250
                });
                if (isUp) timeBar.animate('@animator/bounce_y')
            } else if (isUp) {
                var sign = event.y < timeBar.y ? -1 : 1
                setTimeInMinutes(currentMinutes + sign*5)
            }
            if (isUp) playRipple(event.x + view.x, event.y + view.y)
        }
    }
);
