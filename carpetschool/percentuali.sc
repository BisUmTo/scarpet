__config() -> {
    'commands' -> {
        '<risp>' -> '_risposta',
        'skip' -> '_unfreeze',
        'decimali <bool>' -> _(b) -> global_decimali = b
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
global_percentuale = null;
global_decimali = true;

// UTILS
_mcd(a,b) -> (
    while(b != 0, 127,
        [a, b] = [b, a % b];
    );
    a
);
_mcm(num1,num2) -> num1*num2/_mcd(num1,num2);
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
_modify_inventory(player, m) -> (
    for([range(inventory_size(player))],
        if(rand(100)< 4 * global_domanda,
            item_tuple = inventory_get(player, _i);
            if(item_tuple,
                [item, count, nbt] = item_tuple;
                count = min(ceil(count * (100+m*global_percentuale)/100),999999);
                if(nbt:'Damage', nbt:'Damage'+= -global_percentuale*m);
                inventory_set(player, _i, count, item, nbt)
            )
        )
    );
);

// DOMANDA RISPOSTA
global_primi = [1,2,3,5,7,11,13,17,19];
_domanda() -> (
    _freeze();
    print('=====================================================');
    print(format('b#ff0000 MATEMATICA CON MINECRAFT') +  ' #4.' + (global_domanda+=1));
    print(format('i Rispondi correttamente per ricevere un premio!\n'));

    tipo = floor(rand(3));
    if(
        tipo == 0,
            global_percentuale = floor(rand(101));
            if(global_decimali,
                numero = floor(rand(101));
                mcd = floor(rand(20));
            , // else
                if(global_percentuale <= 1,
                    mcd = 1;
                    numero = floor(rand(201)),
                    mcd = _mcd(100,global_percentuale);
                    numero = 100/mcd;
                );
                if(numero < 40,
                    numero = numero*(floor(rand(floor(rand(global_primi))+1))+1),
                    numero = numero*(floor(rand(floor(rand(8))+1))+1);
                );
            );
            print(format(
                ' Quanto è il ',
                str('b %d%%',global_percentuale),
                '  di ',
                str('b %d',numero),
                ' ?'
            ));
            percentuale = global_percentuale;
            global_percentuale += 100,
        tipo == 1, // elif
            global_percentuale = floor(rand(101));
            if(global_decimali,
                numero = floor(rand(101));
                mcd = floor(rand(20));
            , // else
                if(global_percentuale <= 1, // divisione per 0 ??
                    mcd = 1;
                    numero = floor(rand(201)),
                    mcd = _mcd(100-global_percentuale,global_percentuale);
                    numero = 100/mcd;
                );
                if(numero < 40,
                    numero = numero*(floor(rand(floor(rand(global_primi))+1))+1),
                    numero = numero*(floor(rand(floor(rand(8))+1))+1);
                );
            );

            print(format(
                ' Se a ',
                str('b %d',numero),
                '  togliamo il ',
                str('b %d%%',global_percentuale),
                ' ?'
            ));
            percentuale=100-global_percentuale;
        , // else
            global_percentuale = floor(rand(101));
            if(global_decimali,
                numero = floor(rand(101));
                mcd = floor(rand(20));
            , // else
                mcd = _mcd(100+global_percentuale,global_percentuale);
                numero = 100/mcd;
            );

            if(numero < 40,
                numero = numero*(floor(rand(floor(rand(global_primi))+1))+1),
                numero = numero*(floor(rand(floor(rand(8))+1))+1);
            );

            print(format(
                ' Se a ',
                str('b %d',numero),
                '  aggiungiamo il ',
                str('b %d%%',global_percentuale),
                ' ?'
            ));
            percentuale=global_percentuale+100;
    );
    global_risposta_corretta = percentuale * numero / 100;

    if(global_decimali,
        r1 = global_risposta_corretta + if(!rand(2),-1,1)*floor((rand(10)+1)*100)/100;
        r2 = global_risposta_corretta + if(!rand(2),-1,1)*floor((rand(10)+1)*100)/100;
    , // else
        r1 = global_risposta_corretta + if(!rand(2),-1,1)*(floor(rand(10))+1);
        r2 = global_risposta_corretta + if(!rand(2),-1,1)*(floor(rand(10))+1);
        if(!rand(6), r1 = mcd*(floor(rand(floor(rand(20))+1))+1));
        if(!rand(6), r2 = mcd*(floor(rand(floor(rand(20))+1))+1));
        r1 = floor(r1);
        r2 = floor(r2);
    );

    if(r1 == global_risposta_corretta,
        r1 += if(!rand(2),-1,1)*(floor(rand(10))+1)
    );
    while(r2 == global_risposta_corretta || r2 == r1, 127,
        r2 += if(!rand(2),-1,1)*(floor(rand(10))+1)
    );

    possibili_risposte = [global_risposta_corretta,r1];
    if(r2 != global_risposta_corretta && r2 != r1,
        possibili_risposte += r2
    );
    risposte_disordinate = _c_shuffle(possibili_risposte);

    global_n_risposta_corretta = risposte_disordinate~global_risposta_corretta;

    for(risposte_disordinate,
        print(format(' ' + global_lettere:_i, '!/percentuali '+_i)+' '+format(' ? = '+_,'!/percentuali '+_i)),
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
            if(global_percentuale,
                _modify_inventory(p,1)
            );
        ,   // SBAGLIATA
            particle('wax_on', pos(p)+[0,p~'eye_height',0]+p~'look');
            print(format('#ffdd00 Accidenti! La risposta corretta era la '+global_lettere:global_n_risposta_corretta));
            if(global_percentuale,
                _modify_inventory(p,-1)
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
        fly_speed(_, 0.03);
        delete(global_pos:_)
    );
);
_unfreeze();

// EVENTI
__on_statistic(player, category, item, count) ->
if(category == 'used' && (item~'_pickaxe$'||item~'_axe$'||item~'_hoe$'||item~'_shovel$'),
    global_item = [count, item];
    if(world_time()-global_countdown > global_time, _domanda());
);
