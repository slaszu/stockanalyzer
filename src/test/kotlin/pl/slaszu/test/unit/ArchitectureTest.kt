package pl.slaszu.test.unit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchitectureTest {
    @Test
    fun stockanalyzer_context_test() {
        val stockanalyzerClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val stockanalyzerRule = classes().that().resideInAPackage("..pl.slaszu.stockanalyzer..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..pl.slaszu.stockanalyzer..")

        stockanalyzerRule.check(stockanalyzerClasses);
    }

    @Test
    fun recommendation_context_test() {
        val recommendationClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val recommendationRule = classes().that().resideInAPackage("..pl.slaszu.recommendation..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..pl.slaszu.recommendation..")

        recommendationRule.check(recommendationClasses);
    }

    @Test
    fun shared_kernel_context_test() {
        val sharedKernelClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val sharedKernelRule = noClasses().that().resideInAPackage("..pl.slaszu.shared_kernel..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..pl.slaszu.stockanalyzer..",
                "..pl.slaszu.recommendation.."
            )

        sharedKernelRule.check(sharedKernelClasses);
    }

    @Test
    fun ddd_test() {
        val allClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.slaszu")

        val ruleDomain = classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(
                "..domain..",
                "..infrastructure..",
                "..application.."
            )

        ruleDomain.check(allClasses);

        val ruleApplication = classes().that().resideInAPackage("..application..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(
                "..userinterface..",
                "..application..",
            )

        ruleApplication.check(allClasses);

        val ruleInfrastructure = classes().that().resideInAPackage("..infrastructure..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(
                "..infrastructure.."
            )

        ruleInfrastructure.check(allClasses);
    }
}