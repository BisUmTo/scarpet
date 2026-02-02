//////
// autocommand script by BisUmTo
// adds /autocommand command:
// /autocommand -> Shows help
// /autocommand get -> Shows current command
// /autocommand set <command> -> Sets command to autorun when a player joins
//////

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global',
    'commands' -> {
        '' -> _() -> _help(),
        'set <text_command>' -> _(txt) -> _set(),
        'get' -> _() -> _get()
    },
    'arguments' -> {
        'text_command' -> {
            'type' -> 'text'
        }
    }
};

global_command = '';
_get() -> (
    if(global_command != '', 
        print(player(), format('b Command curently running:'));
        print(player(), format(str('ig   %s', global_command)))
    , //else
        print(player(), format('b No command setted'));
    )
);

_help() -> (
    print(player(), format('b Usage of /autocommand:'));
    print(player(), format('ig  /autocommand get', '?/autocommand get', '  - Shows command currently running on player join event'));
    print(player(), format('ig  /autocommand set <command>', '?/autocommand set ', '  - Sets command to run on player join event'));
);

_set(txt) -> (
    global_command = txt;
    print(player(), format('b Command setted'));
);

__on_player_connects(player) -> (
    if (query(player, 'player_type') != 'fake',
        run(global_command);
    );
);
