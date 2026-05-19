-- 004_seed_shop_catalog.sql
-- Oktodo — items globales de la tienda

INSERT INTO shop_catalog (id, title, emoji, price, category) VALUES
  ('s1', 'Sombrero mágico',  '🎩',   40, 'Accesorio'),
  ('s2', 'Lentes cool',      '🕶️',  25, 'Accesorio'),
  ('s3', 'Bufanda morada',   '🧣',   30, 'Ropa'),
  ('s4', 'Corona mini',      '👑',   60, 'Premium'),
  ('s5', 'Moño elegante',    '🎀',   20, 'Accesorio'),
  ('s6', 'Traje espacial',   '🧑‍🚀', 90, 'Skin')
ON CONFLICT (id) DO NOTHING;
