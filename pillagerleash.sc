//////
// pillagerleash by BisUmTo
// Right click with a lead on a pillager without any item in its hands in to leash it
//////

global_leash_nbt = if(system_info('game_data_version') >= 3815, 'leash', 'Leash');

__config() -> {'stay_loaded' -> true, 'scope' -> 'global'};

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(player~'gamemode'=='spectator',return());
    if(entity~'type' != 'pillager' || entity~'holds' != null,return());
    if(player~'holds':0 != 'lead',
        if(!player~'holds' && query(player,'holds','offhand'):0 == 'lead',
            hand = 'offhand';
        ,
            return();
        );
    ,
        hand = 'mainhand';
    );
    if(parse_nbt(entity~'nbt'):global_leash_nbt:'UUID',return());

    __leash_to(player,entity);

    modify(player, 'swing', hand);
    if(player~'gamemode' != 'creative',
        slot = if(hand=='mainhand',player~'selected_slot',-1);
        prev = inventory_get(player,slot);
        inventory_set(player,slot,(prev:1)-1,prev:0,prev:2);
    );
);

__leash_to(player,entity) -> (
    run(str('data modify entity %s '+global_leash_nbt+'.UUID set from entity %s UUID',entity~'uuid',player~'uuid'));
);
