//////
// lodestonebuttons by BisUmTo (idea by Demi-Wrath)
// Place a button on a lodestone and link a compass to the lodestone
// Right click holding a lodestone compass will activate the button on the linked lodestone
//////

__on_player_uses_item(player, item_tuple, hand)->(
    if(!item_tuple || item_tuple:0 != 'compass' || player~'trace' == 'lodestone', return());
    [item, count, nbt] = item_tuple;
    if(!nbt || !(pos = nbt:'LodestonePos'), return());
    dimension = nbt:'LodestoneDimension';
    pos = [pos:'X', pos:'Y', pos:'Z'];

    in_dimension(dimension,
        pos_button = pos_offset(pos,'up');
        if((block = block(pos_button)) ~ '_button$' == null, 
            sound('block.stone_button.click_off', pos(player), 1, 0.7);
            return()
        );
        state = block_state(block);
        if(state:'face' != 'floor' || state:'powered' == 'true', 
            sound('block.stone_button.click_off', pos(player), 1, 0.7);
            return()
        );

        state:'powered' = 'true';
        set(pos_button, block, state);
        for(diamond(pos_button,2,2), update(_));
        sound('block.stone_button.click_on', pos(player), 1, 1.2);

        schedule(20, _(outer(block),outer(pos_button),outer(state),outer(player))->(
            if(block(pos_button) == block,
                state:'powered' = 'false';
                set(pos_button, block, state);
                for(diamond(pos_button,2,2), update(_));
                sound('block.stone_button.click_off', pos(player));
            )
        ))
    )
)
