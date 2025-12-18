package com.finpro.twogoods.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ProductSeed {

	private final JdbcTemplate jdbcTemplate;

	record ProductTemplate(
			String name,
			String description,
			List<String> categories,
			List<String> imageUrls
	) {}

	private final List<ProductTemplate> products = List.of(

			new ProductTemplate(
					"Men's Cotton T-Shirt",
					"Comfortable breathable cotton t-shirt for everyday use",
					List.of("Men", "Shirt", "Unisex"),
					List.of(
							"https://images.unsplash.com/photo-1521572163474-6864f9cf17ab",
							"https://images.unsplash.com/photo-1512436991641-6745cdb1723f",
							"https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb"
						   )
			),

			new ProductTemplate(
					"Women's Summer Dress",
					"Lightweight floral summer dress",
					List.of("Women", "Unisex"),
					List.of(
							"https://images.unsplash.com/photo-1520975869019-4b8ec9f8a3a0",
							"https://images.unsplash.com/photo-1520975916090-3105956dac38",
							"https://images.unsplash.com/photo-1490481651871-ab68de25d43d"
						   )
			),

			new ProductTemplate(
					"Kids Denim Pants",
					"Durable denim pants for active kids",
					List.of("Child", "Pants"),
					List.of(
							"https://images.unsplash.com/photo-1618354691303-dc1b1c63e3db",
							"https://images.unsplash.com/photo-1514996937319-344454492b37",
							"https://images.unsplash.com/photo-1523381294911-8d3cead13475"
						   )
			),

			new ProductTemplate(
					"Baby Cotton Onesie",
					"Soft cotton onesie for babies",
					List.of("Baby", "Unisex"),
					List.of(
							"https://images.unsplash.com/photo-1602810318383-e386cc6f1d69",
							"https://images.unsplash.com/photo-1602810318383-e386cc6f1d69",
							"https://images.unsplash.com/photo-1602810318383-e386cc6f1d69"
						   )
			),

			new ProductTemplate(
					"Leather Wallet",
					"Genuine leather wallet with multiple compartments",
					List.of("Accessory", "Men"),
					List.of(
							"https://images.unsplash.com/photo-1598032895397-b9472444bf93",
							"https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb",
							"https://images.unsplash.com/photo-1585386959984-a4155224a1ad"
						   )
			),

			new ProductTemplate(
					"Unisex Baseball Cap",
					"Classic adjustable baseball cap",
					List.of("Accessory", "Unisex"),
					List.of(
							"https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f",
							"https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb",
							"https://images.unsplash.com/photo-1503342217505-b0a15ec3261c"
						   )
			)
														  );

	public void seed() {

		List<Long> merchantIds = jdbcTemplate.queryForList(
				"SELECT user_id FROM merchant_profile",
				Long.class
														  );

		if (merchantIds.isEmpty()) return;

		Random random = new Random();

		for (int i = 0; i < 200; i++) {

			ProductTemplate template =
					products.get(random.nextInt(products.size()));

			Long merchantId =
					merchantIds.get(random.nextInt(merchantIds.size()));

			Long productId = jdbcTemplate.queryForObject("""
                INSERT INTO products
                (created_at, name, description, price, is_available, condition, merchant_id)
                VALUES (now(), ?, ?, ?, true, ?, ?)
                RETURNING id
            """,
														 Long.class,
														 template.name(),
														 template.description(),
														 BigDecimal.valueOf(75_000 + random.nextInt(425_000)),
														 random.nextBoolean() ? "NEW" : "USED",
														 merchantId
														);

			// === 3 IMAGES
			for (String image : template.imageUrls()) {
				jdbcTemplate.update("""
                    INSERT INTO product_images
                    (created_at, image_url, product_id)
                    VALUES (now(), ?, ?)
                """, image + "?w=800", productId);
			}

			// === MULTIPLE CATEGORIES
			for (String category : template.categories()) {
				jdbcTemplate.update("""
                    INSERT INTO product_categories
                    (product_id, categories)
                    VALUES (?, ?)
                """, productId, category);
			}
		}
	}
}
