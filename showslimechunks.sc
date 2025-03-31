// Adds /showslimechunks command to the game
// /showslimechunks - Toggles slime chunks
// /showslimechunks on - Shows slime chunks
// /showslimechunks off - Hides slime chunks
// /showslimechunks toggle - Toggles slime chunks
// /showslimechunks radius <n> - Sets the radius of the slime chunks

__config() -> {
  'commands' -> {
    '' -> _() -> (global_status = !global_status),
    'on' -> _() -> (global_status = true),
    'off' -> _() -> (global_status = false),
    'toggle' -> _() -> (global_status = !global_status),
    'radius <radius>' -> _(radius) -> (global_chunk_radius = radius),
  },
  'arguments' -> {
    'radius' -> {
      'type' -> 'int',
      'min' -> 1,
      'max' -> 64,
      'suggest' -> [5, 10, 16, 32, 64]
    },
  }
};

global_status = true;
global_chunk_radius = 5;

__on_tick() ->
if(global_status && tick_time() % 20 == 0,
    p = player();

    [px,py,pz] = map(pos(p), floor(_/16)*16);

    c_for(dx = -global_chunk_radius*16, dx <= global_chunk_radius*16, dx += 16,
        c_for(dz = -global_chunk_radius*16, dz <= global_chunk_radius*16, dz += 16,
            x = px + dx;
            z = pz + dz;
            if (in_slime_chunk(x, py, z),
                draw_shape('box', 21, {
                    'from' -> [x, -64, z],
                    'to' -> [x+16, 448, z+16],
                    'fill' -> 0x00ff0033,
                    'player' -> p,
                    'color' -> 0x00ff0055
                })
            )
        )
    )
)