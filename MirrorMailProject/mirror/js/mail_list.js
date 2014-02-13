$('@id/pencil').animate({
    properties: {
        scale: [2, 1]
    },
    interpolator: '@android:interpolator/bounce',
    duration: 1000
    })

$('@id/avatar').on('click',
     function(view, event) {
         view.animate('@animator/card_flip_right_out',
             function(animationEvent) {
                 var tick = $('@id/tick', view.parent)
                 tick.visibility = 'visible'
                 view.visibility = 'invisible'
                 tick.animate('@animator/card_flip_left_in')
            })
     })
