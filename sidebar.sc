__config() -> {
    'stay_loaded' -> true,
    'commands' -> {
        '' -> _() -> run('scoreboard objectives setdisplay sidebar'),
        '<objective>' -> _(score) -> run('scoreboard objectives setdisplay sidebar ' + score:0)
    }
}
