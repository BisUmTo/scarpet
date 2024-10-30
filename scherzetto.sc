//////
// scherzetto by BisUmTo
// Aggiunge il comando /scherzetto permette di ottenere oggetti per scherzi
// altrimenti questi oggetti possono essere ottenuti dando una torta o un biscotto ad uno zombie con una jack o lantern in testa
//////

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global',
    'commands' -> {
        '' -> _() -> _give_scherzetto(pos(player()),null, true, player()),
        'kill' -> _() -> _kill(),
        '<scherzo>' -> _(scherzo) -> _give_scherzetto(pos(player()),scherzo, true, player())
    },
    'arguments' -> {
        'scherzo' -> {
            'type' -> 'text',
            'suggester' -> _(args) -> keys(global_scherzetti)
        }
    }
};

global_heads = {
    'Michael Myers' -> ['1074180241,1384924072,-1239733646,-913276546','eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjAwMGNlNzI2NDAwOTFmNzUyODE5NWNiODA3Yzc1MDQ2ZjZiMmFiZjE5MDEwNzNiMDEyZGRlYzFjYTJlMDYyNCJ9fX0='],
    'UWU Pumpkin' -> ['242274431,-906607362,-1144166545,-1237805604','eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdkODRiOTc0YzVlZDUzMDA0ZjQ3NzI5OTI3MDNkNmUwOTY1OTY4ZmFmMTNkNGFhYzVhYmQ1YTcwNjM3Y2ViNyJ9fX0='],
    'Evil Pumpkin' -> ['-1434891341,-1233761407,-1438407316,186835026','eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUzZWU2YmJlMmQ1YWEyYTA5YjIzZTA4NGJkNTJlY2QwMTJkYjVhNmFmMzhlOThkMGUzMzM5ZjkxMWE5MmVjIn19fQ=='],
    'Masked Ghost' -> ['-565937884,1762345403,-1279205962,1223271951','eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzZWJkMDQ3MmY5NDY0Nzc1ZjMyMjBlYmNkNTQ2NWE4MmEzZjY2NGEzYTcyMGU4ZTU5YWYxZmI2YmJhOWU3NiJ9fX0=']
};

global_scherzetti = {
    'Orda di pipistrelli' -> _(pos) -> (
        loop(50,
            bat = spawn('bat', pos, '{PersistenceRequired:true,Tags:["scherzetto"]}');
            entity_event(bat, 'on_death', _(entity, reason) -> (
                if(rand(5), return());
                head = rand(keys(global_heads));
                spawn('item', pos(entity), str('{Item:{id:"minecraft:player_head",count:1,components:{"minecraft:custom_name":\'{"text":"%s","color":"gold","underlined":true,"bold":true,"italic":false}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\'],"minecraft:profile":{id:[I;%s],properties:[{name:"textures",value:"%s"}]}}},Motion:[%.2ff,0.2f,%.2ff],PickupDelay:10}', 
                      head, global_heads:head:0, global_heads:head:1, rand(0.2)-0.1, rand(0.2)-0.1))
            ));
            sound('entity.goat.screaming.death', pos(bat), 1, 1, 'master');
        )
    ),
    'Phantom gigante' -> _(pos) -> (
        phantom = spawn('phantom', pos+[0,40,0], '{Size:80,PersistenceRequired:true,Tags:["scherzetto"],attributes:[{id:"minecraft:max_health",base:100}],Health:100,active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}');
        entity_event(phantom, 'on_tick', _(entity) -> (
            modify(entity, 'nbt_merge', '{HasVisualFire:0b, Fire:-20}')
        ));
        entity_event(phantom, 'on_death', _(entity, reason) -> (
            loop(5,
                head = rand(keys(global_heads));
                spawn('item', pos(entity), str('{Item:{id:"minecraft:player_head",count:1,components:{"minecraft:custom_name":\'{"text":"%s","color":"gold","underlined":true,"bold":true,"italic":false}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\'],"minecraft:profile":{id:[I;%s],properties:[{name:"textures",value:"%s"}]}}},Motion:[%.2ff,0.2f,%.2ff],PickupDelay:10}', 
                    head, global_heads:head:0, global_heads:head:1, rand(0.2)-0.1, rand(0.2)-0.1))
            )
        ));
        sound('entity.allay.death', pos, 1, 2, 'master');
    ),
    'Attacco dei giganti' -> _(pos) -> (
        giant = spawn('giant', pos, '{PersistenceRequired:true,Tags:["scherzetto"],attributes:[{id:"minecraft:max_health",base:100}],Health:100,active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}');
        entity_event(giant, 'on_tick', _(entity) -> (
            modify(entity, 'nbt_merge', '{HasVisualFire:0b, Fire:-20}');
            if(rand(200), return());
            sound('entity.zombie.ambient', pos(entity), 2, 0.5, 'master');
        ));
        entity_event(giant, 'on_damaged', _(entity, amount, source, attacking_entity) -> (
            sound('entity.zombie.hurt', pos(entity), 2, 0.5, 'master');
        ));
        entity_event(giant, 'on_death', _(entity, reason) -> (
            loop(5,
                head = rand(keys(global_heads));
                spawn('item', pos(entity), str('{Item:{id:"minecraft:player_head",count:1,components:{"minecraft:custom_name":\'{"text":"%s","color":"gold","underlined":true,"bold":true,"italic":false}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\'],"minecraft:profile":{id:[I;%s],properties:[{name:"textures",value:"%s"}]}}},Motion:[%.2ff,0.2f,%.2ff],PickupDelay:10}', 
                    head, global_heads:head:0, global_heads:head:1, rand(0.2)-0.1, rand(0.2)-0.1))
            )
        ));
        sound('entity.zombie.hurt', pos, 1, 0.5, 'master');
    ),
    'Conigli impazziti' -> _(pos) -> (
        loop(20,
            rabbit = spawn('rabbit', pos, '{PersistenceRequired:true,Tags:["scherzetto"],RabbitType:99,DeathLootTable:"minecraft:empty"}');
            entity_event(rabbit, 'on_death', _(entity, reason) -> (
                if(rand(5), return());
                head = rand(keys(global_heads));
                spawn('item', pos(entity), str('{Item:{id:"minecraft:player_head",count:1,components:{"minecraft:custom_name":\'{"text":"%s","color":"gold","underlined":true,"bold":true,"italic":false}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\'],"minecraft:profile":{id:[I;%s],properties:[{name:"textures",value:"%s"}]}}},Motion:[%.2ff,0.2f,%.2ff],PickupDelay:10}', 
                      head, global_heads:head:0, global_heads:head:1, rand(0.2)-0.1, rand(0.2)-0.1))
            ));
            sound('entity.goat.screaming.death', pos(rabbit), 1, 1, 'master');
        )
    ),
    'Zucche indemoniate' -> _(pos) -> (
        loop(24,
            spider = spawn('magma_cube', pos, '{Size:0,Silent:1b,PersistenceRequired:true,Tags:["scherzetto"],active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],DeathLootTable:"minecraft:empty"}');
            size = rand(0.5)+0.5;
            jack_o_lantern = spawn('block_display', pos, nbt(str('{
                    teleport_duration:2,
                    block_state:{
                        Name:"minecraft:%s",
                        Properties:{facing:"south"}
                    },
                    Tags:["scherzetto"],
                    transformation:{
                        left_rotation:[0f,0f,0f,1f],
                        right_rotation:[0f,0f,0f,1f],
                        translation:[%ff,%ff,%ff],
                        scale:[%ff,%ff,%ff]
                    }
                }', 
                rand(['jack_o_lantern','carved_pumpkin']),
                -size/2, -min(size,spider~'height'), -size/2, 
                size, size, size
            )));
            modify(jack_o_lantern, 'mount', spider);
            entity_event(spider, 'on_death', _(entity, reason) -> (
                if(rand(5), return());
                head = rand(keys(global_heads));
                spawn('item', pos(entity), str('{Item:{id:"minecraft:player_head",count:1,components:{"minecraft:custom_name":\'{"text":"%s","color":"gold","underlined":true,"bold":true,"italic":false}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\'],"minecraft:profile":{id:[I;%s],properties:[{name:"textures",value:"%s"}]}}},Motion:[%.2ff,0.2f,%.2ff],PickupDelay:10}', 
                      head, global_heads:head:0, global_heads:head:1, rand(0.2)-0.1, rand(0.2)-0.1))
            ));
            entity_event(jack_o_lantern, 'on_tick', _(e,outer(spider)) -> (
                if(!query(e, 'mount'), modify(e, 'remove'));
                modify(e,'pitch', spider~'pitch');
                modify(e,'yaw', spider~'yaw');
            ));
            entity_event(spider, 'on_tick', _(entity) -> (
                if(rand(400), return());
                sound('entity.witch.celebrate', pos(entity), 2, 0.5, 'master');
            ));
            entity_event(spider, 'on_damaged', _(entity, amount, source, attacking_entity) -> (
                sound('block.pumpkin.carve', pos(entity), 2, 0.5, 'master');
            ));
        );
    )
};

_scherzetto(nome,pos,player) -> {
    if(nome == null || keys(global_scherzetti) ~ nome == null,
        nome = rand(keys(global_scherzetti))
    );
    print(player('*'),format(str('bd %s ha fatto uno scherzetto', player), str('ig  "%s"', nome)));
    call(global_scherzetti:nome, pos);
};

_give_scherzetto(pos, nome, requires_op, player) -> {
    if(requires_op && player~'permission_level' < 1, 
        print(player, format('r Non hai i permessi per eseguire questo comando'));
        return()
    );
    if(nome == null || keys(global_scherzetti) ~ nome == null,
        nome = rand(keys(global_scherzetti))
    );
    // /give @p splash_potion[potion_contents={custom_color:16748800},max_stack_size=16,custom_name='{"bold":true,"color":"gold","italic":false,"text":"%s","underlined":true}',custom_data={scherzetto:"%s"}] 1
    item = spawn('item', pos, str('{Item:{id:"minecraft:splash_potion",count:1,components:{"minecraft:hide_additional_tooltip":{},potion_contents:{custom_color:16748800},max_stack_size:16,custom_name:\'{"bold":true,"color":"gold","italic":false,"text":"%s","underlined":true}\',"minecraft:lore":[\'{"text":"Esclusiva Halloween 2024","color":"gray","italic":true}\',\'{"text":"Scherzetto di %s","color":"#505050","italic":true}\'],custom_data:{scherzetto:"%s",player:"%s"}}}}', nome, player, nome, player));
    modify(item, 'accelerate', (pos(player)-pos)*0.1+[0,0.2,0]);
    nbt = item~'nbt';
    nbt:'Owner' = player~'nbt':'UUID';
    modify(item, 'nbt', nbt);
};

entity_load_handler('potion',
    _(e, new) -> (
        if(!new, return());
        nbt = query(e, 'nbt');
        if((nome = nbt:'Item.components."minecraft:custom_data".scherzetto') == null, return());
        player = nbt:'Item.components."minecraft:custom_data".player';
        entity_event(e, 'on_removed', _(entity, outer(nome), outer(player)) -> (
            _scherzetto(nome, pos(entity), player(player));
        ));
    )
);

_handle_zombie(e, new) -> (
    // if(!new, return());
    head_item = inventory_get(e,4);
    if(head_item == null || head_item:0 != 'jack_o_lantern', return());
    entity_event(e, 'on_damaged', _(entity, amount, source, attacking_entity) -> (
        if(attacking_entity == null || attacking_entity~'type' != 'player', return());
        item_tuple = attacking_entity~'holds';
        if(item_tuple == null, return());
        [item, count, nbt] = item_tuple;
        pos = pos(entity)+[0,entity~'eye_height',0];
        if(item == 'cake',
            for(keys(global_scherzetti),
                _give_scherzetto(pos, _, false, attacking_entity);
            ),
        // elif
            item == 'cookie',
            _give_scherzetto(pos, null, false, attacking_entity),
        // else
            return()
        );
        sound('entity.allay.death', pos(entity), 1, 0.5, 'hostile');
        if(attacking_entity~'gamemode' != 'creative',
            inventory_set(player, player~'selected_slot', count - 1, item, nbt);
        );
    ));
    entity_event(e, 'on_tick', _(entity) -> (
        if(rand(10), return());
        particle('item{item:"minecraft:jack_o_lantern"}', pos(entity)+[rand(1)-0.5,1.5,rand(1)-0.5], 2, 0.2, 0.1);
    ));
);

entity_load_handler('zombie', '_handle_zombie');
for(entity_list('zombie'), _handle_zombie(_,false));

_kill() -> {
    for(filter(entity_list('*'), query(_,'has_scoreboard_tag','scherzetto')), modify(_,'remove'));
};
