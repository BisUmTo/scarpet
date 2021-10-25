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
global_item = null;

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
_mcd(a,b) -> (
    while(b != 0, 127,
        [a, b] = [b, a % b];
    );
    a
);

// ITEM POOL
global_item_pool = _remove_match(item_list(), '_spawn_egg$');
delete(global_item_pool, 0);
delete(global_item_pool, global_item_pool~'enchanted_golden_apple');
delete(global_item_pool, global_item_pool~'debug_stick');

// STAMPA PROPORZIONE
_proporzione(p) -> _incognita(p:0) + ' : ' + _incognita(p:1) + ' = ' + _incognita(p:2) + ' : ' + _incognita(p:3);
_s_proporzione(p) -> _proporzione(
    t = if(rand(2), p, [p:3,p:2,p:1,p:0]);
    t = if(rand(2), t, [t:1,t:0,t:3,t:2]);
);
_incognita(i) -> if(i!=null, str(i), '?');

// OPERAZIONI PROPORZIONI
_per(k, p) -> _perL(k, _perR(k, p));
_perL(k, p) -> (
    p:0 = p:0 * k;
    p:1 = p:1 * k;
    p
);
_perR(k, p) -> (
    p:2 = p:2 * k;
    p:3 = p:3 * k;
    p
);
_semplifica(p) -> _semplificaL(_semplificaR(p));
_semplificaL(p) -> (
    mcd = max(1, _mcd(p:0, p:1));
    p:0 = p:0 / mcd;
    p:1 = p:1 / mcd;
    p
);
_semplificaL(p) -> (
    mcd = max(1, _mcd(p:2, p:3));
    p:2 = p:2 / mcd;
    p:3 = p:3 / mcd;
    p
);

// DOMANDA RISPOSTA
global_primi = [1,2,3,5,7,11,13,17,19,23];
_casuale() -> (
    rapporto = rand(global_primi);
    b1 = floor(rand(8))+1;
    b2 = floor(rand(8))+1;
    if(b1 == b2, b1 += if(!rand(2),-1,1)*(floor(rand(10))+1));
    if(b1 == 0, b1 += if(!rand(2),-1,1)*(floor(rand(10))+1));
    f = [b1, b1*rapporto, b2, b2*rapporto];
    if(b1*rapporto < 50,
        f = _perL(floor(rand(5)+1), f);
    );
    if(b2*rapporto < 50,
        f = _perR(floor(rand(5)+1), f);
    );
    f
);

_domanda() -> (
    _freeze();
    print('=====================================================');
    print(format('b#ff0000 MATEMATICA CON MINECRAFT') +  ' #2.' + (global_domanda+=1));
    print(format('i Rispondi correttamente per ricevere un premio!\n'));

    proporzione = _casuale();
    index = floor(rand(4));
    global_risposta_corretta = proporzione:index;
    proporzione:index = null;
    print(format('b ' + _s_proporzione(proporzione)));

    if(index == 0,
        r1 = proporzione:1 * proporzione:3 / proporzione:2;
        r2 = proporzione:2 * proporzione:3 / proporzione:1,
       index == 1;
        r1 = proporzione:0 * proporzione:2 / proporzione:3;
        r2 = proporzione:2 * proporzione:3 / proporzione:0,
       index == 2;
        r1 = proporzione:1 * proporzione:3 / proporzione:0;
        r2 = proporzione:0 * proporzione:1 / proporzione:3,
       index == 3;
        r1 = proporzione:0 * proporzione:2 / proporzione:1;
        r1 = proporzione:0 * proporzione:1 / proporzione:2,
    );
    if(rand(6), r1 = (floor(rand(25))+1));
    if(rand(6), r2 = (floor(rand(25))+1));
    r1 = floor(r1);
    r2 = floor(r2);
    if(r1 == global_risposta_corretta,
        r1 += if(!rand(2),-1,1)*(floor(rand(10))+1)
    );
    while(r2 == global_risposta_corretta || r2 == r1, 127,
        r2 += if(!rand(2),-1,1)*(floor(rand(10))+1)
    );

    possibili_risposte = [global_risposta_corretta,r1,r2];
    risposte_disordinate = _c_shuffle(possibili_risposte);

    global_n_risposta_corretta = risposte_disordinate~global_risposta_corretta;

    for(risposte_disordinate,
        print(format(' ' + global_lettere:_i, '!/proporzioni '+_i)+' '+format(' ? = '+_,'!/proporzioni '+_i)),
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
            if(global_item,
                spawn('item',pos(p),parse_nbt({'Item'->{'id'->global_item:1,'Count'->global_item:0}}));
            );
        ,   // SBAGLIATA
            particle('wax_on', pos(p)+[0,p~'eye_height',0]+p~'look');
            print(format('#ffdd00 Accidenti! La risposta corretta era la '+global_lettere:global_n_risposta_corretta));
            if(global_item,
                run(str('/clear %s %s %d', player(), global_item:1, global_item:0));
            );
        );
        global_n_risposta_corretta = null;
    );
    _unfreeze();
);

// FREEZE
global_pos = {};
run('carpet creativeFlySpeed 0');
_freeze() -> (
    if(run('tick freeze'):1:0~'normally',run('tick freeze'));
    for(player('all'),
        global_pos:_ = pos(_);
        fly_speed(_, 0);
        modify(_, 'motion', 0, 0, 0);
        modify(_, 'gamemode', 'spectator')
    )
);
_unfreeze() -> (
    if(run('tick freeze'):1:0~'frozen',run('tick freeze'));
    for(player('all'),
        if(global_pos:_,
            modify(_, 'gamemode', 'survival');
            modify(_, 'pos', global_pos:_)
        );
        delete(global_pos:_)
    );
);
_unfreeze();

// EVENTI
__on_statistic(player, category, item, count) ->
if(category == 'crafted',
    global_item = [count, item];
    if(world_time()-global_countdown > global_time, _domanda());
);
