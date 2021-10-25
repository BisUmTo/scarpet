global_delimiter = ' ';
global_hex = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'];
global_colors = ['black', 'dark_blue', 'dark_green', 'dark_aqua', 'dark_red', 'dark_purple', 'gold', 'gray', 'dark_gray', 'blue', 'green', 'aqua', 'red', 'light_purple', 'yellow', 'white'];
global_saplings = ['acacia_sapling','birch_sapling','dark_oak_sapling','jungle_sapling','oak_sapling','jungle_sapling'];

__config() -> {
    'stay_loaded' -> true,
    'global' -> true,
    'commands' -> {
        '' -> '_help',
        'sapling' -> '_help_sapling',
        'sapling <sapling>' -> '_set_sapling',
        'give' -> '_help_give',
        'give <nome>' -> _(nome) -> _give_sapling(player(),nome,null),
        'give <nome> <colore>' -> _(nome, colore) -> _give_sapling(player(),nome,colore-'#'),
        'event' -> '_help_event',
        'event <eventi>' -> '_set_event'
    },
    'arguments' -> {
        'sapling' -> {
            'type' -> 'text',
            'suggester' -> _(args) -> (
                arg = args:'sapling' || '';
                items = split(global_delimiter,arg);
                if(length(items) && split('',arg):(-1) != global_delimiter,
                    delete(items, -1);
                );
                if(items,
                    items_str = join(global_delimiter, items);
                    map(global_saplings, items_str + global_delimiter + _),
                // else
                    global_saplings
                )
            )
        },
        'nome' -> {
            'type' -> 'string',
            'suggest' -> ['BisUmTo', 'Hemerald96', 'HarryPotter']
        },
        'colore' -> {
            'type' -> 'term',
            'suggest' -> global_colors
        },
        'eventi' -> {
            'type' -> 'term',
            'options' -> ['entrambi','sub','follow','mai']
        }
    }
};

_help() -> print(player(), 'Ogni volta che un utente lascerà un sub/follow, ti verrà dato un albero');
_help_sapling() -> print(player(), 'Con il comando `/twitch_spawn sapling <lista>` puoi scegliere la lista di sapling possono esserti dati');
_help_give() -> print(player(), 'Con il comando `/twitch_spawn give <nome> [colore]` puoi ottenere un albero con un determinato nome e, opzionalmente, colore');
_help_event() -> print(player(), 'Con il comando `/twitch_spawn event <evento>` puoi scegliere gli eventi che ti daranno l\'albero');

_set_sapling(str) -> (
    s = split(' ',str);
    if(length(s) == 0, exit('Devi selezionare almeno un tipo di sapling'));
    nbt = parse_nbt(nbt_storage('alberi_sc'));
    nbt:'saplings' = s;
    nbt_storage('alberi_sc',encode_nbt(nbt));
    print(player(),'I sapling ottenibili sono ora:');
    print(player(),join(', ',s));
);

_set_event(str) -> (
    if(
        str=='mai', r=0,
        str=='follow', r=1,
        str=='sub', r=2,
        str=='entrambi', r=3
    );
    nbt = parse_nbt(nbt_storage('alberi_sc'));
    nbt:'events' = r;
    nbt_storage('alberi_sc',encode_nbt(nbt));
    print(player(),'Eventi aggiornati correttamente');
);

if(!nbt_storage('alberi_sc'),
    saplings = filter(global_saplings,_!='dark_oak_sapling');
    nbt = encode_nbt({'saplings'->saplings,'events'->3});
    nbt_storage('alberi_sc',nbt);
);

_give_sapling(player, actor, color) -> (
    if(!color, color = str('%06X', rand(16777216)));
    if(global_colors~color==null, color = '#'+color);
    run(str(
        'give %s %s{display:{Name:\'{"text":"%s","color":"%s"}\'},alberi_sc:true}',
        player~'command_name',
        rand(parse_nbt(nbt_storage('alberi_sc'):'saplings')),
        actor,
        color 
    ))
);

_sign_face(p) ->(
    ['north','east','south','west']:(floor((p~'yaw'+45)/90)%4)
);

_place_sign(block, entity) -> (
    facing = first(entity~'scoreboard_tags',_~'alberi_sc_\\w+')-'alberi_sc_';
    nome = entity~'nbt':'CustomName';
    sign = block-'_log'+'_wall_sign';
    pos = pos_offset(pos_offset(pos(block), facing), 'up');
    set(pos, sign, 'facing', facing, str(
        '{Text2:\'%s\'}',
        nome
    ))
);

__on_player_places_block(player, item_tuple, hand, block)->
if(item_tuple:2:'alberi_sc',
    name = item_tuple:2:'display.Name';
    spawn('armor_stand', pos(block)+[0.5,0.75,0.5], str(
        '{Marker:true,Invisible:true,NoGravity:true,CustomNameVisible:true,CustomName:\'%s\',Tags:["alberi_sc","alberi_sc_%s"]}',
        name,
        _sign_face(player)
    ))
);

__on_tick()->
for(entity_selector('@e[type=armor_stand,tag=alberi_sc]'),
    b=block(pos(_));
    if(b~'_sapling' == null,
        if(b~'_log' != null,
            _place_sign(b,_)
        );
        i = entity_selector(str(
            '@e[type=item,limit=1,sort=nearest,x=%d,y=%d,z=%d,nbt=!{Item:{tag:{display:{}}}},nbt={PickupDelay:9s},distance=..5]',
            pos(b);
        ));
        if(i,
            name = _~'nbt':'CustomName'; 
            modify(i:0,'nbt_merge',str(
                '{Item:{tag:{display:{Name:\'%s\'},alberi_sc:true}}}',
                name
            ))
        );
        modify(_, 'remove');
    )
);

__on_twitch_follow(player, actor) -> (
    events = parse_nbt(nbt_storage('alberi_sc')):'events';
    if(floor(events/1)%2, _give_sapling(player, actor, null));
);

__on_twitch_subscription(player, actor, message, tier, months, resubbed, streak, gifted) -> (
    events = parse_nbt(nbt_storage('alberi_sc')):'events';
    if(floor(events/2)%2, _give_sapling(player, actor, null));
);
