package pl.slaszu.unit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchitectureTest {
    @Test
    fun stockanalyzer_context_test() {
        val allClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val stockanalyzerRule = classes().that().resideInAPackage("..pl.slaszu.stockanalyzer..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(
                "..pl.slaszu.stockanalyzer..",
                "..event..",
                "..application.."
            )

        stockanalyzerRule.check(allClasses);
    }

    @Test
    fun recommendation_context_test() {
        val allClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val recommendationRule = classes().that().resideInAPackage("..pl.slaszu.recommendation..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(
                "..pl.slaszu.recommendation..",
                "..event..",
                "..application.."
            )


        recommendationRule.check(allClasses);
    }

    @Test
    fun shared_kernel_context_test() {
        val allClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val sharedKernelRule = noClasses().that().resideInAPackage("..pl.slaszu.shared_kernel..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..pl.slaszu.stockanalyzer..",
                "..pl.slaszu.recommendation..",
                "..pl.slaszu.blog.."
            )

        sharedKernelRule.check(allClasses);
    }

    @Test
    fun layer_test() {
        val allClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val layeredArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Userinterface").definedBy("..userinterface..")

            .whereLayer("Domain").mayNotAccessAnyLayer()

            .whereLayer("Application").mayOnlyBeAccessedByLayers("Userinterface")
            .whereLayer("Application").mayOnlyAccessLayers("Domain")

            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain")

            .whereLayer("Userinterface").mayNotBeAccessedByAnyLayer()
            .whereLayer("Userinterface").mayOnlyAccessLayers("Application", "Domain")



        layeredArchitecture.check(allClasses)
    }
}