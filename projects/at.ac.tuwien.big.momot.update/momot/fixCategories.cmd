rem FeaturesAndBundlesPublisher
rem java -jar C:\Users\Martin\software\eclipse-modeling-mars-2\plugins\org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\ -artifactRepository file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\ -source . -configs gtk.linux.x86 -compress -publishArtifacts

rem UpdateSitePublisher
rem java -jar C:\Users\Martin\software\eclipse-modeling-mars-2\plugins\org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher -metadataRepository file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\ -artifactRepository file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\ -source . -compress -publishArtifacts

java -jar C:\Users\Martin\software\eclipse-modeling-mars-2\plugins\org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar -application org.eclipse.equinox.p2.publisher.CategoryPublisher -metadataRepository file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\ -categoryDefinition file:D:\tuwien\repos\artist\02_development\MOMoT\projects\at.ac.tuwien.big.momot.update\momot\site.xml -categoryQualifier at.ac.tuwien.big.momot.category.momot -compress 