---
title: MOMoT Generic Modularization Case Study
layout: index
---

## Generic Modularization Case Study
This material accompanies the publication to the [Modularity in Modelling Workshop (MOMO)](http://www.momo2016.ece.mcgill.ca/).
We show the application of our generic modularization approach on the example of Ecore.

### Meta-Model
<div style="text-align:center">
<img src="http://martin-fleck.github.io/momot/images/casestudy/generic_modularization/generic_modularization_metamodel.svg" alt="Modularization Meta-Model" />
</div>

In our generic modularization meta-model, a language is the root of the object graph.
A ``Language`` consists of a set of ``Modules`` which in turn consist of entities. In the unmodularized case we only have one module containing all entities. 
``Entities`` are identified by their name and may have relationships among them (e.g., associations or inheritance).
A ``Relationship`` is given a weight that is considered during the search process. 
The higher the weight the more important the relationship is, i.e., the closer the respective elements should be grouped together.
A common, abstract superclass ``NamedElement`` ensures that all elements in the system have a name.

## Ecore 2 Generic Modularization
For example, when we aim to modularize Ecore models, we could provide the following mapping.

<table style="text-align:center">
<thead>
<tr>
<th>Ecore</th>
<th>Generic Modularization Language</th>
</tr>
</thead>
<tbody>
<tr>
<td>EPackage</td>
<td>Module</td>
</tr>
<tr>
<td>EClass</td>
<td>Entity</td>
</tr>
<tr>
<td>EDataType</td>
<td>Entity</td>
</tr>
<tr>
<td>EEnum</td>
<td>Entity</td>
</tr>
<tr>
<td>eSuperType</td>
<td>Relationship (weight=2)</td>
</tr>
<tr>
<td>EAttribute</td>
<td>Relationship (weight=1)</td>
</tr>
<tr>
<td>EReference (not containment)</td>
<td>Relationship (weight=1)</td>
</tr>
<tr>
<td>EReference (containment)</td>
<td>Relationship (weight=3)</td>
</tr>
</tbody>
</table>

This mapping is realized using an ATL transformation. 
A whole project with this transformation can be downloaded in [our repository](https://github.com/martin-fleck/momot/blob/master/projects/at.ac.tuwien.big.momot.examples.ecore.transformation.zip). 
An excerpt of the transformation is shown below:

```
rule EPackage_Language {
  from
    p : Ecore!EPackage, 
    c : Ecore!EClass (p.refImmediateComposite().oclIsUndefined() and 
      Ecore!EPackage.allInstancesFrom('IN').first() = p and 
      Ecore!EClass.allInstancesFrom('IN').first() = c)
  to 
    l : Module!Language (
      name <- p.name, 	
      modules <- Ecore!EPackage.allInstancesFrom('IN')
    )
}

rule Relationship(target : Ecore!EClassifier, weight : Integer) {
  to 
    r : Module!Relationship (
      relationshipEnd <- target,
      weight <- weight
    )
  do{ r; }
}

rule Entity_Enum {
  from
    enum : Ecore!EEnum
  to 
    e : Module!Entity (
      name <- enum.name
    )
}

...
```

### Rules

**Move Entity:**
Since at the beginning there is only one module with all in the input model, we create modules to which the entities can be moved. 
This rule moves an entity with the name ``entity`` from a module with the name ``source`` to another entity with the name ``target``.

<div style="text-align:center">
<img src="http://martin-fleck.github.io/momot/images/casestudy/generic_modularization/generic_modularization_rules.svg" alt="Modularization Meta-Model" />
</div>

### Objectives and Constraints
Since modularization is such a common and well-studied problem, many metrics have been proposed which indicate the quality of a module.
Common metrics include coupling and cohesion.
For our example, we follow the *Equal-Size Cluster Approach*, as described by Praditwong et al in *Software Module Clustering as a Multi-Objective Search Problem*.
The goal of this approach is to produce equally-sized modules, i.e., modules that have a similar number of entities. 
Therefore, besides the above mentioned two objectives we also aim to maximize the number of modules and minimize the difference between the minimum and maximum number of entities in a module.
In order to improve efficiency, we have outsourced evaluation of the objectives and constraints into a separate class ([MetricsCalculator](https://github.com/martin-fleck/momot/blob/master/projects/at.ac.tuwien.big.momot.examples.ecore/src/at/ac/tuwien/big/momot/examples/ecore/fitness/metric/MetricsCalculator.java)), which calculates the values in one iteration through the model.
In the configuration example below, you can find how this external calculation can be integrated into the fitness evaluation of MOMoT.

**Coupling:**
Coupling refers to the number of external relationships a specific module has, i.e., the sum of inter-relationships with other modules.
Typically, low coupling is preferred as this indicates that a group covers separate functionality aspects of a system, improving the maintainability, readability and testability of the overall system.
In our case study, not all relationships are considered equal, therefore the coupling is the sum of all inter-relationship weights instead of just the number of all inter-relationships. 

**Cohesion:**
Cohesion refers to the relationships within a module, i.e., the sum of intra-relationships in the module.
As opposed to coupling, the cohesion within one module should be maximized to ensure that it does not contain parts that are not part of its functionality.
In our case study, not all relationships are considered equal, therefore the cohesion is the sum of all intra-relationship weights instead of just the number of all intra-relationships.

**Number of Modules:**
We aim to maximize the number of modules to avoid having all entities in a single large module.

**Min-Max Difference:**
The difference between the module with the lowest number of entities and the module with the highest number of entities should be minimized.
By doing so, we aim to produce equally-sized modules as the optimal difference would be 0.

```
fitness = {
  preprocess = { // use attribute storage for external calculation
    val root = MomotUtil.getRoot(solution.execute, typeof(Language))
    solution.setAttribute("metrics", MetricsCalculator.calculate(root))
  }
  objectives = { 
    Coupling : minimize { // java-like syntax
      val metrics = solution.getAttribute("metrics", typeof(LanguageMetrics))
      metrics.coupling
    }
    Cohesion : maximize { 
      val metrics = solution.getAttribute("metrics", typeof(LanguageMetrics))
      metrics.cohesion
    }
    NrModules : maximize {
      (root as Language).^modules.filter[m | !m.entities.empty].size
    }
    MinMaxDiff : minimize {
      val sizes = (root as Language).^modules.filter[m | !m.entities.empty].map[m | m.entities.size]
      sizes.max - sizes.min
    }  
  }
}
```

### References
* Praditwong K, Harman M, Yao X. Software Module Clustering as a Multi-Objective Search Problem. IEEE
Transactions on Software Engineering 2011; 37(2):264–282, doi:[10.1109/TSE.2010.26](http://dx.doi.org/10.1109/TSE.2010.26).
* [Example project on GitHub](https://github.com/martin-fleck/momot/tree/master/projects/at.ac.tuwien.big.momot.examples.ecore)

### Input Examples
As an example input model, we show the modularization of four Ecore-based languages: HTML, JAVA, OCL, and QVT.
The initial values for the case studies are given in the following table.
The second module for HTML and OCL is retrieved from the PrimitiveTypes package which contain the data types Integer, Boolean, and String for both case studies, and additionaly Double for OCL.
The eight modules for the QVT case studies are retrieved from the following packages: QVT Template, Imperative OCL, EMOF, QVT Operational, QVT Core, QVT Base, QVT Relation, and Essential OCL.

<table>
<thead>
<tr>
<th></th>
<th style="text-align:center">HTML</th>
<th style="text-align:center">JAVA</th>
<th style="text-align:center">OCL</th>
<th style="text-align:center">QVT</th>
</tr>
</thead>
<tbody>
<tr>
<td>Entities</td>
<td style="text-align:right">62</td>
<td style="text-align:right">132</td>
<td style="text-align:right">77</td>
<td style="text-align:right">151</td>
</tr>
<tr>
<td>Coupling</td>
<td style="text-align:right">0</td>
<td style="text-align:right">0</td>
<td style="text-align:right">0</td>
<td style="text-align:right">216</td>
</tr>
<tr>
<td>Cohesion</td>
<td style="text-align:right">119</td>
<td style="text-align:right">856</td>
<td style="text-align:right">257</td>
<td style="text-align:right">587</td>
</tr>
<tr>
<td>Modules</td>
<td style="text-align:right">2</td>
<td style="text-align:right">1</td>
<td style="text-align:right">2</td>
<td style="text-align:right">8</td>
</tr>
<tr>
<td>MinMaxDiff</td>
<td style="text-align:right">56</td>
<td style="text-align:right">0</td>
<td style="text-align:right">69</td>
<td style="text-align:right">38</td>
</tr>
</tbody>
</table> 

### Output
As for each case study we get a lot of solutions, we apply a knee point strategy to select one solution.
The values for the modularization results are as follow:

<table>
<thead>
<tr>
<th></th>
<th style="text-align:center">HTML</th>
<th style="text-align:center">JAVA</th>
<th style="text-align:center">OCL</th>
<th style="text-align:center">QVT</th>
</tr>
</thead>
<tbody>
<tr>
<td>Coupling</td>
<td style="text-align:right">18</td>
<td style="text-align:right">339</td>
<td style="text-align:right">42</td>
<td style="text-align:right">355</td>
</tr>
<tr>
<td>Cohesion</td>
<td style="text-align:right">101</td>
<td style="text-align:right">517</td>
<td style="text-align:right">262</td>
<td style="text-align:right">448</td>
</tr>
<tr>
<td>Modules</td>
<td style="text-align:right">5</td>
<td style="text-align:right">7</td>
<td style="text-align:right">4</td>
<td style="text-align:right">8</td>
</tr>
<tr>
<td>MinMaxDiff</td>
<td style="text-align:right">31</td>
<td style="text-align:right">2</td>
<td style="text-align:right">45</td>
<td style="text-align:right">2</td>
</tr>
</tbody>
</table> 

Please note that for the QVT case study where already a lot of modules where available, the solution we have selected seems to focus on the MinMaxDiff objective and therefore the coupling and cohesion values for this solution are a bit worse than in the initial version. 