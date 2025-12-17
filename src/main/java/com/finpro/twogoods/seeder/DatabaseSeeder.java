package com.finpro.twogoods.seeder;

import com.finpro.twogoods.entity.seed.SeedHistory;
import com.finpro.twogoods.repository.seed.SeedHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

	private final JdbcTemplate jdbcTemplate;
	private final UserSeed userSeed;
	private final CustomerSeed customerSeed;
	private final MerchantSeed merchantSeed;
	private final ProductSeed productSeed;
	private final SeedHistoryRepository seedHistoryRepository;

	@Override
	public void run(ApplicationArguments args) {
		if (seedHistoryRepository.existsBySeedName("INITIAL_SEED")) {
			return;
		}

		seedHistoryRepository.save(SeedHistory.builder()
											  .seedName("INITIAL_SEED")
											  .executedAt(LocalDateTime.now())
											  .build());
		seed("V1_USERS", userSeed::seed);
		seed("V2_CUSTOMERS", customerSeed::seed);
		seed("V3_MERCHANTS", merchantSeed::seed);
		seed("V4_PRODUCTS", productSeed::seed);
	}

	private void seed(String name, Runnable action) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM seed_history WHERE seed_name = ?",
				Integer.class,
				name
												   );

		if (count != null && count > 0) {
			System.out.println("⏭ " + name + " already seeded");
			return;
		}

		action.run();

		jdbcTemplate.update(
				"INSERT INTO seed_history VALUES (?, now())",
				name
						   );

		System.out.println("✅ " + name + " seeded");
	}
}

