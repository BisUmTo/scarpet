//////
// barrier by BisUmTo
// Allows players to break barriers in survival mode
//
// Known issues:
// - can_break doesn't check into block tags
//////

__config() -> {'scope'->'global','stay_loaded'->true};

//Constants
global_durability = {'wooden' -> 59, 'stone' -> 131, 'iron' -> 250, 'diamond' -> 1561, 'netherite' -> 2031};
global_candestroy_nbt = if(system_info('game_data_version') >= 3819, 'components.minecraft:can_break.predicates[].blocks', 'CanDestroy');

__on_player_clicks_block(player, block, face) -> (
    g = player ~ 'gamemode';
    if(g == 'spectator' || g == 'creative' || block != 'barrier', return());
    item_tuple = player ~ 'holds';
    if((g == 'adventure' && !item_tuple && !item_tuple:2:global_candestroy_nbt ~ '"minecraft:barrier"'), return());
    if (item_tuple:0 ~ '_pickaxe$',
        (
        schedule(0, '_break', player, pos(block), str(block), 1, 0);
        ),
        schedule(0, '_break', player, pos(block), str(block), 5, 0);
    )
);

global_damage_nbt = if(system_info('game_data_version') >= 3837, 'components.minecraft:damage', 'Damage');

_break(player, pos, block_name, step, lvl) -> (
   current = player~'active_block';
   if (current != block_name || pos(current) != pos,
      modify(player, 'breaking_progress', null);
   ,
      modify(player, 'breaking_progress', lvl);
      if (lvl >= 10, (
        destroy(pos, -1);
        item_tuple = inventory_get(player, player ~ 'selected_slot');
        if (!item_tuple, return());
        [item, count, nbt] = item_tuple;
        if (item ~ '_axe$' || item ~ '_pickaxe$' || item ~ '_shovel$' || item ~ '_sword$' || item ~ '_hoe$' || item ~ 'shears', (
        nbt:global_damage_nbt = nbt:global_damage_nbt + 1;
        if (nbt:global_damage_nbt < global_durability:(split('_', item):0),
            inventory_set(player, player ~ 'selected_slot', count, item, nbt), (
            inventory_set(player, player ~ 'selected_slot', 0);
            sound('entity.item.break', player ~ 'pos', 1.0, 1.0, 'player');
            ))
        ));
      ));
      schedule(step, '_break', player, pos, block_name, step, lvl+1)
   );
);

global_barrier_particle = if(system_info('game_data_version') >= 3829, 'block_marker{block_state:{Name:"barrier"}}',
                             system_info('game_data_version') >= 2830, 'block_marker barrier',
                             'barrier');

_show_barrier(player, pos) -> particle(global_barrier_particle, pos + [0.5, 0.5, 0.5], 1, 0, 0, player);
_show_barrier_area(player) ->
in_dimension(player,
    scan(pos(player), [5, 5, 5],
        if(_ == 'barrier',
            _show_barrier(player, pos(_))
        )
    )
);

__on_player_switches_slot(player, from, to)->
if((item_tuple = inventory_get(player, to)) && item_tuple:0 == 'barrier' || (item_tuple = inventory_get(player, -1)) && item_tuple:0 == 'barrier',
    _show_barrier_area(player)
);

__on_explosion_outcome(pos, power, source, causer, mode, fire, blocks, entities) -> (
if (system_info('world_carpet_rules'):'explosionNoBlockDamage' == 'true' || (system_info('world_gamerules'):'mobGriefing' == 'false' && source != 'Primed TNT' && source != 'Player'), return());
in_dimension(query(source, 'dimension'),
    scan(pos, [power, power, power],
        if(_ == 'barrier',
            destroy(pos(_), -1)
        )
    )
)
);

__on_player_places_block(player, item_tuple, hand, block) -> (
if((item_tuple = player ~ 'holds') && item_tuple:0 == 'barrier' || (item_tuple = inventory_get(player, -1)) && item_tuple:0 == 'barrier',
    _show_barrier_area(player)
)
);

_looper() -> (
    for(filter(player('*'), (h = _ ~ 'holds') && h:0 == 'barrier' && _ ~ 'gamemode' != 'creative' || (h = inventory_get(_, -1)) && h:0 == 'barrier' && _ ~ 'gamemode' != 'creative'),
        _show_barrier_area(_);
    );
    schedule(40, '_looper')
);
_looper();
