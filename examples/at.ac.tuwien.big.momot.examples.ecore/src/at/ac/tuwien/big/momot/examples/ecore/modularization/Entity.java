/**
 */
package at.ac.tuwien.big.momot.examples.ecore.modularization;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link at.ac.tuwien.big.momot.examples.ecore.modularization.Entity#getRelationships <em>Relationships</em>}</li>
 * </ul>
 *
 * @see at.ac.tuwien.big.momot.examples.ecore.modularization.ModularizationPackage#getEntity()
 * @model
 * @generated
 */
public interface Entity extends NamedElement {
   /**
    * Returns the value of the '<em><b>Relationships</b></em>' containment reference list.
    * The list contents are of type {@link at.ac.tuwien.big.momot.examples.ecore.modularization.Relationship}.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Relationships</em>' containment reference list isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    *
    * @return the value of the '<em>Relationships</em>' containment reference list.
    * @see at.ac.tuwien.big.momot.examples.ecore.modularization.ModularizationPackage#getEntity_Relationships()
    * @model containment="true"
    * @generated
    */
   EList<Relationship> getRelationships();

} // Entity
