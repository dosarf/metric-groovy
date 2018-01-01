package io.github.dosarf

import static org.assertj.core.api.Assertions.*
import org.junit.Test

class DudeTest {

	@Test
	void defaultDudeIsCalledJohnDudesonDoe() {
		assertThat(Dude.defaultDude().name).isEqualTo('John (Dudeson) Doe')
	}
	
	@Test
	void dudeCanHaveArbitraryName() {
		assertThat(new Dude(name: 'Killroy').name).isEqualTo('Killroy')
	}
}
