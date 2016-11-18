/*
 * generated by Xtext
 */
package at.ac.tuwien.big.momot.lang;

import at.ac.tuwien.big.momot.lang.scoping.MOMoTScopeProvider;

import com.google.inject.Binder;

import org.eclipse.xtext.generator.IGenerator;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class MOMoTRuntimeModule extends at.ac.tuwien.big.momot.lang.AbstractMOMoTRuntimeModule {
   @Override
   public Class<? extends IGenerator> bindIGenerator() {
      return super.bindIGenerator();
   }

   @Override
   public void configureIScopeProviderDelegate(final Binder binder) {
      binder.bind(org.eclipse.xtext.scoping.IScopeProvider.class)
            .annotatedWith(com.google.inject.name.Names
                  .named(org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
            .to(MOMoTScopeProvider.class);
   }
}