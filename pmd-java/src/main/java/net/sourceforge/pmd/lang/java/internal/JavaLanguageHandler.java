/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.ast.internal.ReportingStrategy;
import net.sourceforge.pmd.lang.java.metrics.JavaMetricsProvider;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.java.xpath.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.xpath.JavaFunctions;
import net.sourceforge.pmd.lang.java.xpath.MetricFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeIsExactlyFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeIsFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeOfFunction;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

import net.sf.saxon.sxpath.IndependentContext;

public class JavaLanguageHandler extends AbstractPmdLanguageVersionHandler {

    private final LanguageLevelChecker<?> levelChecker;
    private final LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> myMetricsProvider = new JavaMetricsProvider();

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        super(JavaProcessingStage.class);
        this.levelChecker = new LanguageLevelChecker<>(jdkVersion, preview, ReportingStrategy.reporterThatThrows());
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaParser(levelChecker, parserOptions);
    }


    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler() {
            @Override
            public void initialize() {
                TypeOfFunction.registerSelfInSimpleContext();
                GetCommentOnFunction.registerSelfInSimpleContext();
                MetricFunction.registerSelfInSimpleContext();
                TypeIsFunction.registerSelfInSimpleContext();
                TypeIsExactlyFunction.registerSelfInSimpleContext();
            }

            @Override
            public void initialize(IndependentContext context) {
                super.initialize(context, LanguageRegistry.getLanguage(JavaLanguageModule.NAME), JavaFunctions.class);
            }
        };
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return JavaRuleViolationFactory.INSTANCE;
    }


    @Override
    public LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }
}