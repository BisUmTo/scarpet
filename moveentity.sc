import('math','_euclidean');

__config()-> {
    'scope' -> 'global',
    'commands'->{
        '' -> _() -> _comando(1, [player() ~ ['trace', 5, 'entities']], player_x(player()), player_y(player()), player_z(player())),
        '<numberOfBlocks>' -> _(nob) -> _comando(nob, [player() ~ ['trace', 5, 'entities']], player_x(player()), player_y(player()), player_z(player())),
        '<numberOfBlocks> <entities> ' -> _(nob, ent) -> _comando(nob, ent, player_x(player()), player_y(player()), player_z(player())),
        '<numberOfBlocks> <entities> north' -> _(nob, ent) -> _comando(nob, ent, 0, 0, -1),
        '<numberOfBlocks> <entities> south' -> _(nob, ent) -> _comando(nob, ent, 0, 0, 1),
        '<numberOfBlocks> <entities> east' -> _(nob, ent) -> _comando(nob, ent, 1, 0, 0),
        '<numberOfBlocks> <entities> ovest' -> _(nob, ent) -> _comando(nob, ent, -1, 0, 0),
        '<numberOfBlocks> <entities> up' -> _(nob, ent) -> _comando(nob, ent, 0, 1, 0),
        '<numberOfBlocks> <entities> down' -> _(nob, ent) -> _comando(nob, ent, 0, -1, 0),
    },
    'arguments' -> {
        'numberOfBlocks' -> {'type' -> 'int', 'min' -> 1, 'max' -> 72000, 'suggest' -> [1]},
    }
};

_comando(numberOfBlocks, entities, x1, y1, z1) ->
if(entities:0 == null,
    print(player(), format('r No entity found'))
, player() ~ 'permission_level' >= 2, // elif
    for(entities,
        e = _;
        i += 1;
        [x, y, z] = query(e, 'pos');
        x += x1 * numberOfBlocks;
        y += y1 * numberOfBlocks;
        z += z1 * numberOfBlocks;
        modify(e, 'pos', [x, y, z])
    );
    print(player(), str('Affected %d entit%s', i, if(i==1,'y','ies')));
, // else
    for(entities,
        if(_euclidean(pos(player()), pos(_)) <= 5,
            i += 1;
            e = _;
            i += 1;
            [x, y, z] = query(e, 'pos');
            x += x1 * numberOfBlocks;
            y += y1 * numberOfBlocks;
            z += z1 * numberOfBlocks;
            modify(e, 'pos', [x, y, z])
        , // else
            j += 1
        )
    );
    print(player(), str('Affected %d entit%s', i, if(i==1,'y','ies')));
    print(player(), format(str('r %d entit%s too far', j, if(j==1,'y was','ies were'))))
);

player_x(player) -> sin(player~'yaw');
player_y(player) -> -cos(player~'yaw');
player_z(player) -> -sin(player~'pitch')