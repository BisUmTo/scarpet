__config() -> {
    'scope' -> 'global'
};

global_app_name = system_info('app_name');
create_datapack(global_app_name, {
    'data' -> { 'minecraft' -> { 'tags' -> { 'blocks' -> { 'enderman_holdable.json' -> {
        'replace' -> true,
        'values' -> [
          'minecraft:podzol',
          'minecraft:pumpkin',
          'minecraft:carved_pumpkin',
          'minecraft:melon',
          'minecraft:mycelium'
        ]
    } } } } }
});

__on_close()-> (
    run('datapack disable "file/'+global_app_name+'.zip"')
)
