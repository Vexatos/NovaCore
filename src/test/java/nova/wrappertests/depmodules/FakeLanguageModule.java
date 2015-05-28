package nova.wrappertests.depmodules;

import nova.core.util.LanguageManager;
import se.jbee.inject.bind.BinderModule;

/**
 * @author Calclavia
 */
public class FakeLanguageModule extends BinderModule {

	@Override
	protected void declare() {
		bind(LanguageManager.class).to(FakeLanguageManager.class);
	}

	public static class FakeLanguageManager extends LanguageManager {
		@Override
		public String translate(String key) {
			return key;
		}
	}
}
