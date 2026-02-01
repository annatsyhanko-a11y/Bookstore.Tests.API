package com.assessment.bookstore.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.restassured.response.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.UUID;

@Aspect
public class AllureStepAspect {

    @Around("execution(public * com.assessment.bookstore..*Client.*(..))")
    public Object aroundClientCall(ProceedingJoinPoint pjp) throws Throwable {
        return runAsAllureStep(pjp, stepName(pjp, "When"));
    }

    @Around(
            "execution(public static * com.assessment.bookstore..*Asserts.*(..)) || " +
                    "execution(public static * com.assessment.bookstore..*Assertions.*(..)) || " +
                    "execution(public * com.assessment.bookstore..*Asserts.*(..)) || " +
                    "execution(public * com.assessment.bookstore..*Assertions.*(..)) || " +

                    "execution(public static * assertions..*Asserts.*(..)) || " +
                    "execution(public static * assertions..*Assertions.*(..)) || " +
                    "execution(public * assertions..*Asserts.*(..)) || " +
                    "execution(public * assertions..*Assertions.*(..))"
    )
    public Object aroundAssertionCall(ProceedingJoinPoint pjp) throws Throwable {
        return runAsAllureStep(pjp, stepName(pjp, "Verify"));
    }

    private Object runAsAllureStep(ProceedingJoinPoint pjp, String stepName) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String stepId = UUID.randomUUID().toString();

        StepResult step = new StepResult().setName(stepName);

        String[] paramNames = sig.getParameterNames();
        Object[] args = pjp.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
                String value = safeArgToString(args[i]);
                step.getParameters().add(new Parameter().setName(name).setValue(value));
            }
        }

        Allure.getLifecycle().startStep(stepId, step);

        try {
            Object result = pjp.proceed();
            Allure.getLifecycle().updateStep(stepId, s -> s.setStatus(Status.PASSED));
            return result;

        } catch (Throwable t) {
            Allure.getLifecycle().updateStep(stepId, s -> {
                s.setStatus(Status.FAILED);
                s.setStatusDetails(new StatusDetails().setMessage(safeMsg(t)));
            });
            throw t;

        } finally {
            Allure.getLifecycle().stopStep(stepId);
        }
    }

    private static String stepName(ProceedingJoinPoint pjp, String prefix) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        return prefix + ": " + sig.getDeclaringType().getSimpleName() + "." + sig.getName();
    }


    private static String safeArgToString(Object v) {
        if (v == null) return "<null>";
        try {
            if (v instanceof Response r) {
                String ct = r.getHeader("Content-Type");
                return "Response{status=" + r.getStatusCode() + ", contentType=" + (ct == null ? "<null>" : ct) + "}";
            }
            String s = String.valueOf(v);
            return s.length() > 300 ? s.substring(0, 300) + "...(truncated)" : s;
        } catch (Exception e) {
            return "<toString failed: " + e.getClass().getSimpleName() + ">";
        }
    }

    private static String safeMsg(Throwable t) {
        if (t == null) return "Unknown error";
        String msg = t.getMessage();
        return (msg == null || msg.isBlank()) ? t.getClass().getName() : (t.getClass().getName() + ": " + msg);
    }
}
