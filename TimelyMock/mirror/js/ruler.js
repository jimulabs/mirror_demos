function setRulerLabelParams(view, alpha, scale, y) {
    var rulerText = $('@id/rulerText', view)
    rulerText.alpha = alpha
    rulerText.textSize = 8 * scale
    view.y = y - view.height/2
//    $('@id/rulerLine', view).scaleX = scale
}

function showNormalLabelOnRuler(view, index) {
    var labelInterval = 6
    var ruler = view.parent
    var y = index / (ruler.children.size()) * ruler.height
    setRulerLabelParams(view, index%labelInterval==0 ? 1 : 0, 1, y)
}

function initRuler() {
    var ruler = $('@id/ruler')
    var children = ruler.children
    for (i=0; i<children.size(); i++) {
        showNormalLabelOnRuler(children.get(i), i)
    }
}

function magnifyRuler(ratio) {
    var ruler = $('@id/ruler')

    var cellCount = 25
    var zoomWindowHeight = ruler.height * .25
    var zoomWindowRadius = 3, cellsInWindow = zoomWindowRadius*2 + 1
    var zoomedUnitHeight = zoomWindowHeight / cellsInWindow
    var unzoomedCellHeight = (ruler.height-zoomWindowHeight)/(cellCount-cellsInWindow)
    var windowCenter = Math.floor(cellCount * ratio)
    var windowTop = Math.max(0, windowCenter - zoomWindowRadius)
    var windowBottom = Math.min(cellCount-1, windowCenter + zoomWindowRadius)

    var children = ruler.children, childCount = children.size()
    var maxScale = 3, maxOffset = zoomWindowHeight*0.5, slope = 0.6
    for (i=0; i<childCount; i++) {
        var offset, alpha, scale, y
        if (windowTop<=i && i<=windowBottom) {
            var unzoomedOffset = (windowCenter - zoomWindowRadius)*unzoomedCellHeight
            var baseY = unzoomedOffset + (i-windowTop)*zoomedUnitHeight
            var r = 1-Math.abs((i-windowCenter))/zoomWindowRadius*slope
            alpha = r
            scale = maxScale * r
            var offset = maxOffset * (i-windowCenter)/zoomWindowRadius
            y = baseY + offset
        } else {
            y = i*unzoomedCellHeight
            scale = 1
            alpha = i%6==0 ? 1 : 0
        }

        var v = children.get(i)
        setRulerLabelParams(v, alpha, scale, y)
    }
}

initRuler()