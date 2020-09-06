__config() -> {'scope'->'global','stay_loaded'->true};
__command() -> '';

show(value) -> (run(str('/scoreboard objectives setdisplay sidebar %s',value)));
