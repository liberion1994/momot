/*
 * generated by Xtext
 */
package at.ac.tuwien.big.momot.lang.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;

import at.ac.tuwien.big.momot.lang.ui.hover.MOMoTDispatchingTextHover;
import at.ac.tuwien.big.momot.lang.ui.hover.MOMoTHoverProvider;

/**
 * Use this class to register components to be used within the IDE.
 */
public class MOMoTUiModule extends at.ac.tuwien.big.momot.lang.ui.AbstractMOMoTUiModule {
	public MOMoTUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return MOMoTHoverProvider.class;
	}
	
	@Override
	public Class<? extends IEObjectHover> bindIEObjectHover() {
		return MOMoTDispatchingTextHover.class;
	}
}