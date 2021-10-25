
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
global_slot = null;

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

// ENCH
_enchant(item_tuple, id, lvl) -> (
    [item, count, nbt] = item_tuple;
    if(!nbt, nbt = nbt('{}'));
    if(!nbt:'Enchantments', nbt:'Enchantments' = nbt('[]'));
    pnbt = parse_nbt(nbt);
    pnbt:'Enchantments' += {'id' -> id, 'lvl' -> lvl};
    [item, count, encode_nbt(pnbt)]
);
global_enchantments = ['aqua_affinity','bane_of_arthropods','binding_curse','blast_protection','channeling','depth_strider','efficiency','feather_falling','fire_aspect','fire_protection','flame','fortune','frost_walker','impaling','infinity','knockback','looting','loyalty','luck_of_the_sea','lure','mending','multishot','piercing','power','projectile_protection','protection','punch','quick_charge','respiration','riptide','sharpness','silk_touch','smite','soul_speed','sweeping','thorns','unbreaking','vanishing_curse'];
_r_enchant(item_tuple) -> (
    id = rand(global_enchantments);
    lvl = floor(rand(10))+1;
    _enchant(item_tuple, id, lvl)
);

// DOMANDA RISPOSTA
_domanda() -> (
    _freeze();
    print('=====================================================');
    print(format('b#ff0000 MATEMATICA CON MINECRAFT') +  ' #3.' + (global_domanda+=1));
    print(format('i Rispondi correttamente per ricevere un premio!\n'));

    a = floor(rand(500));
    if(!rand(7), a = a*-1);
    b = floor(rand(10))+2;

    print(format(' '+a+' mod '+b+' = ?'));

    global_risposta_corretta = a % b;
    r1 = floor(rand(b));
    if(r1 == global_risposta_corretta,
        r1 = (r1+if(rand(2),-1,1)) % b
    );
    possibili_risposte = [global_risposta_corretta,r1];
    r2 = r1;
    while(r1 == r2 || r2 == global_risposta_corretta, 128,
        r2 = floor(rand(b));
    );
    if(r1 != r2 && r2 != global_risposta_corretta,
        possibili_risposte += r2
    );

    risposte_disordinate = _c_shuffle(possibili_risposte);

    global_n_risposta_corretta = risposte_disordinate~global_risposta_corretta;

    for(risposte_disordinate,
        print(format(' ' + global_lettere:_i, '!/modulo '+_i)+' '+format(' ? = '+_,'!/modulo '+_i)),
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

            // Incantesimo casuale
            item_tuple = inventory_get(p, global_slot);
            if(item_tuple,
                item_tuple = _r_enchant(item_tuple);
                [item, count, nbt] = item_tuple;
                inventory_set(p, global_slot, count, item, nbt)
            )

        ,   // SBAGLIATA
            particle('wax_on', pos(p)+[0,p~'eye_height',0]+p~'look');
            print(format('#ffdd00 Accidenti! La risposta corretta era la '+global_lettere:global_n_risposta_corretta));

            // Shuffle
            inv = _shuffle(inventory_get(p));
            for(inv,
                if(_,
                    inventory_set(p, _i, _:1, _:0, _:2),
                // else
                    inventory_set(p, _i, 0)
                )
            )
        );
        global_n_risposta_corretta = null;
    );
    _unfreeze();
);

// FREEZE
global_pos = {};
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
        fly_speed(_, 0.03);
        delete(global_pos:_)
    );
);
_unfreeze();

// EVENTI
__on_player_switches_slot(player, from, to) -> (
    global_slot = to;
    if(world_time()-global_countdown > global_time, _domanda());
);
__on_player_swaps_hands(player) ->(
    global_slot = -1;
    if(world_time()-global_countdown > global_time, _domanda());
)
