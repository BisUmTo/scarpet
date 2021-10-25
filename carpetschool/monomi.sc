__config() -> {
    'commands' -> {
        '<risp>' -> '_risposta',
        'skip' -> '_unfreeze'
    },
    'arguments' -> {
        'risp' -> {'type' -> 'int', 'min' -> 0, 'max' -> 2}
    }
};

// GLOBALI
global_time = 300;
global_countdown = world_time()-global_time;
global_domanda = 0;
global_n_risposta_corretta = null;
global_lettere = ['a)','b)','c)'];
global_entity = null;
global_item = null;
global_premio = null;

// UTILS
_remove_match(l, match) -> (
    t = [];
    for(l,
        if(_~match == null, t += _)
    );
    t
);
_shuffle(l) -> (

    loop((s=length(l))-1,
        j = rand(s-_)+_;
        t = l:_;
        l:_ = l:j;
        l:j = t
    );
    l
);
_c_shuffle(list) -> (
    l = copy(list);
    _shuffle(l)
);
_inventory_list(inv) -> map([range(inventory_size(inv))],
    inventory_get(inv, _i)
);
_n_inventory_list(inv) -> (
    il = [];
    for(_inventory_list(inv), if(_, il += _));
    il
);

// ITEM POOL
global_item_pool = _remove_match(item_list(), '_spawn_egg$');
delete(global_item_pool, 0);
delete(global_item_pool, global_item_pool~'enchanted_golden_apple');
delete(global_item_pool, global_item_pool~'debug_stick');

// STAMPA POLINOMIO
_polinomio(p) ->
reduce(
    p,
    // SEGNO
    if(_i || _:0 < 0,
        _a += if(_:0 < 0, '- ', '+ ')
    );
    // NUMERICA
    _a += abs(_:0);
    _a += ' ';
    // LETTERALE
    _a += icon(_:1);
    _a += ' ',
    ''
);
_r_polinomio(p) ->
reduce(
    p,
    if(_:0 == 0,
        _a
    , // else
        // SEGNO
        if(_i || _:0 < 0,
            _a += if(_:0 < 0, '- ', '+ ')
        );
        // NUMERICA
        _a += abs(_:0);
        _a += ' ';
        // LETTERALE
        _a += icon(_:1);
        _a += ' '
    ),
    ''
);
_s_polinomio(p) -> _polinomio(_shuffle(p));
_s_r_polinomio(p) -> _r_polinomio(_shuffle(p));

// OPERAZIONI POLINOMI
_riduci(p) -> (
    dic = {};
    for(p,
        dic:(_:1) += _:0
    );
    map(keys(dic),
        [dic:_, _]
    )
);
_somma(p1, p2) -> [...p1, ...p2];
_r_somma(p1, p2) -> riduci(_somma(p1, p2));
_sottrazione(p1, p2) -> _somma(p1, _opposto(p2));
_r_sottrazione(p1, p2) -> riduci(_sottrazione(p1, p2));
_per(k, p) -> map(p, [k * _:0, _:1]);
_opposto(p) -> _per(-1, p);

// OPERAZIONI SPECIALI POLINOMI
_add_rand_item(p1, max_count) ->
p1 += (
    global_premio = [
        floor(rand(max_count))+1,
        rand(global_item_pool)
    ]
);

// DOMANDA RISPOSTA
_casuale() -> (
    random_item = [if(!rand(6),-1,1)*(floor(rand(10))+1), global_item:1];
    polinomio = [global_item, random_item];
    loop(rand(2)+1,
        _add_rand_item(polinomio, if(!rand(6),-1,1)*(floor(rand(10))+1))
    );
    inv = _n_inventory_list(player());
    if(inv,
        loop(rand(2)+1,
            item = rand(inv);
            monomio = [item:1, item:0];
            polinomio += monomio
        );
    );
    polinomio
);

_domanda() -> (
    _freeze();
    print('=====================================================');
    print(format('b#ff0000 MATEMATICA CON MINECRAFT') +  ' #1.' + (global_domanda+=1));
    print(format('i Rispondi correttamente per ricevere un premio!\n'));

    polinomio = _casuale();
    //print(polinomio);
    print(_s_polinomio(polinomio) + '=\n');


    r1 = _riduci(polinomio);
    r2 = copy(r1);
    r3 = copy(r1);
    r2:(rand(length(r2))):0 += if(!rand(6),-1,1)*(floor(rand(10))+1);
    r3:(rand(length(r2))):0 += if(!rand(6),-1,1)*(floor(rand(10))+1);
    possibili_risposte = [r1,r2,r3];
    risposte_disordinate = _c_shuffle(possibili_risposte);
    global_risposta_corretta = possibili_risposte:0;
    global_n_risposta_corretta = risposte_disordinate~global_risposta_corretta;

    for(risposte_disordinate,
        if(rand(2),
            print(format(' ' + global_lettere:_i, '!/monomi '+_i)+' '+_s_polinomio(_)+format('!/monomi '+_i)),
        // else
            print(format(' ' + global_lettere:_i , '!/monomi '+_i)+' '+_s_r_polinomio(_)+format('!/monomi '+_i))
        )
    );
    print('=====================================================');
    global_countdown = world_time();
);
_risposta(risp) -> (
    if(global_n_risposta_corretta != null,
        p = player();
        if(risp == global_n_risposta_corretta,
            // CORRETTA
            particle('happy_villager', pos(p)+[0,p~'eye_height',0]+p~'look');
            print(format('#00ff00 Esattamente! Ecco a te il tuo premio!'));
            if(global_premio,
                spawn('item',pos(p),parse_nbt({'Item'->{'id'->global_premio:1,'Count'->global_premio:0}}));
            );
        ,   // SBAGLIATA
            particle('wax_on', pos(p)+[0,p~'eye_height',0]+p~'look');
            print(format('#ffdd00 Accidenti! La risposta corretta era la '+global_lettere:global_n_risposta_corretta));
            if(global_entity,
                modify(global_entity, 'remove');
            );
        );
        global_n_risposta_corretta = null;
    );
    _unfreeze();
);

// FREEZE
_freeze() -> (
    if(run('tick freeze'):1:0~'normally',run('tick freeze'));
);
_unfreeze() -> (
    if(run('tick freeze'):1:0~'frozen',run('tick freeze'));
);
_unfreeze();

// EVENTI
__on_player_collides_with_entity(player, item_entity) ->
if(item_entity ~ 'pickup_delay' == 0,
    item_tuple = item_entity ~ 'item';
    [item, count, nbt] = item_tuple;

    global_entity = item_entity;
    global_item = [count, item];
    if(world_time()-global_countdown > global_time, _domanda());
);

__on_player_drops_item_after(player, item_entity) -> (
    item_tuple = item_entity ~ 'item';
    [item, count, nbt] = item_tuple;

    global_entity = item_entity;
    global_item = [-count, item];
    if(world_time()-global_countdown > global_time, _domanda());
);
