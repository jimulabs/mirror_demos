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
timeBarParent.on('touch',
    function(view, event) {
        if (event.type == 'move') {
            timeBar.y = Math.max(event.y - timeBarYOffset, timeBarYMargin)
            var hourMinute = event.y * 24 / (timeBarParent.height - timeBar.height)
            var hour = Math.min(24, Math.max(Math.floor(hourMinute), 0))
            var minute = hourMinute - hour>0.5 ? ':30' : ':00'
            timeText.text = hour + minute
        } else {
            var isUp = event.type == 'up';
            var sideX = calcTimeTextSideX(event.x)
            var centerX = (timeBar.width - timeText.width)/2
            var toX = isUp ? centerX : sideX;
            var toScale = isUp ? 1 : 0.8;
            timeText.animate({
                properties: {
                    x: toX,
                    scale: toScale
                },
                interpolator: '@android:interpolator/decelerate_cubic',
                duration: 250
            });
            if (isUp) {
                timeBar.animate('@animator/bounce_y')
                playRipple(event.x + view.x, event.y + view.y)
            }
            if (!isUp) timeBarYOffset = event.y - timeBar.y
        }
    }
);
