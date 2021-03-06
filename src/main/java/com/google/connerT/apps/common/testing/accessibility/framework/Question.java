package com.google.connerT.apps.common.testing.accessibility.framework;

import com.google.connerT.apps.common.testing.accessibility.framework.proto.AccessibilityEvaluationProtos.QuestionProto;
import com.google.connerT.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchy;
import com.google.common.annotations.Beta;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Question generated by a {@link QuestionHandler} about an {@link
 * AccessibilityHierarchyCheckResult}
 */
@Beta
public class Question {

  private final int questionId;
  private final Class<? extends QuestionType> questionTypeClass;
  private final Class<? extends AnswerType> answerTypeClass;
  private final Class<? extends QuestionHandler> questionHandlerClass;
  private final AccessibilityHierarchyCheckResult originalResult;
  private final @Nullable ResultMetadata metadata;

  /**
   * @param questionId the question identifier
   * @param questionTypeClass the class of the type of information needed to answer this question
   * @param answerTypeClass the class of the answer type expected for this question
   * @param questionHandler the {@link QuestionHandler} that created this quesiton
   * @param originalResult the {@link AccessibilityHierarchyCheckResult} that this question is about
   * @param metadata extra data needed to answer this question
   */
  public Question(
      int questionId,
      Class<? extends QuestionType> questionTypeClass,
      Class<? extends AnswerType> answerTypeClass,
      QuestionHandler questionHandler,
      AccessibilityHierarchyCheckResult originalResult,
      @Nullable ResultMetadata metadata) {
    this.questionId = questionId;
    this.questionTypeClass = questionTypeClass;
    this.answerTypeClass = answerTypeClass;
    this.questionHandlerClass = questionHandler.getClass();
    this.originalResult = originalResult;
    this.metadata = metadata;
  }

  /**
   * @param questionId The question identifier
   * @param questionTypeClass the class of the type of information needed to answer this question
   * @param answerTypeClass the class of the answer type expected for this question
   * @param questionHandlerClass the class of the {@link QuestionHandler} that created this question
   * @param originalResult the {@link AccessibilityHierarchyCheckResult} that this question is about
   * @param metadata extra data needed to answer this question
   */
  public Question(
      int questionId,
      Class<? extends QuestionType> questionTypeClass,
      Class<? extends AnswerType> answerTypeClass,
      Class<? extends QuestionHandler> questionHandlerClass,
      AccessibilityHierarchyCheckResult originalResult,
      @Nullable ResultMetadata metadata) {
    this.questionId = questionId;
    this.questionTypeClass = questionTypeClass;
    this.answerTypeClass = answerTypeClass;
    this.questionHandlerClass = questionHandlerClass;
    this.originalResult = originalResult;
    this.metadata = metadata;
  }
  /**
   * Returns the integer id of this question. This id is unique within the class of the
   * QuestionHandler associated with this question. The id is used to differentiate different types
   * of questions produced by a single {@link QuestionHandler}
   *
   * @return the id of this question
   */
  public int getQuestionId() {
    return questionId;
  }

  /** Returns the {@link QuestionType} class for this question */
  public Class<? extends QuestionType> getQuestionTypeClass() {
    return questionTypeClass;
  }

  /** Returns the {@link AnswerType} class for this answer */
  public Class<? extends AnswerType> getAnswerTypeClass() {
    return answerTypeClass;
  }

  /** Returns the {@link QuestionHandler} class that created this question */
  public Class<? extends QuestionHandler> getQuestionHandlerClass() {
    return questionHandlerClass;
  }

  /** Returns the {@link AccessibilityHierarchyCheckResult} that this question is about */
  public AccessibilityHierarchyCheckResult getOriginalResult() {
    return originalResult;
  }

  /**
   * Returns the {@link ResultMetadata} of any additional data needed to ask this question, else
   * returns {@code null} if the question id alone is sufficient
   */
  @Pure
  public @Nullable ResultMetadata getMetadata() {
    return metadata;
  }

  /** Creates a protocol buffer for this {@link Question} following its format */
  public QuestionProto toProto() {
    QuestionProto.Builder builder = QuestionProto.newBuilder();
    builder.setQuestionId(getQuestionId());
    builder.setQuestionTypeClass(getQuestionTypeClass().getName());
    builder.setAnswerTypeClass(getAnswerTypeClass().getName());
    builder.setQuestionHandlerClass(getQuestionHandlerClass().getName());
    builder.setOriginalResult(getOriginalResult().toProto());
    if (getMetadata() instanceof HashMapResultMetadata) {
      builder.setMetadata(((HashMapResultMetadata) getMetadata()).toProto());
    }
    return builder.build();
  }

  /**
   * Creates a {@link Question} from its protocol buffer format.
   *
   * @param proto The protocol buffer representation of an answer, created with {@link #toProto()}
   * @param associatedHierarchy The {@link AccessibilityHierarchy} that this answer is about
   * @throws IllegalArgumentException passing the {@link ClassNotFoundException} if there is an
   *     invalid {@link AnswerType} class in the {@link QuestionProto}. This should not occur as the
   *     {@link QuestionProto} is written in the same environment of ATF in which it is read.
   */
  public static Question fromProto(
      QuestionProto proto, AccessibilityHierarchy associatedHierarchy) {
    int questionId = proto.getQuestionId();
    Class<? extends QuestionType> questionTypeClass = null;
    Class<? extends AnswerType> answerTypeClass = null;
    Class<? extends QuestionHandler> questionHandlerClass = null;

    try {
      questionTypeClass =
          Class.forName(proto.getQuestionTypeClass()).asSubclass(QuestionType.class);
      answerTypeClass = Class.forName(proto.getAnswerTypeClass()).asSubclass(AnswerType.class);
      questionHandlerClass =
          Class.forName(proto.getQuestionHandlerClass()).asSubclass(QuestionHandler.class);

    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    AccessibilityHierarchyCheckResult result =
        AccessibilityHierarchyCheckResult.fromProto(proto.getOriginalResult(), associatedHierarchy);
    HashMapResultMetadata metadata =
        proto.hasMetadata() ? HashMapResultMetadata.fromProto(proto.getMetadata()) : null;

    return new Question(
        questionId, questionTypeClass, answerTypeClass, questionHandlerClass, result, metadata);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Question)) {
      return false;
    }

    Question that = (Question) o;
    if (!getOriginalResult().equals(that.getOriginalResult())) {
      return false;
    }
    if (getQuestionId() != that.getQuestionId()) {
      return false;
    }
    if (getAnswerTypeClass() != that.getAnswerTypeClass()) {
      return false;
    }
    if (getQuestionTypeClass() != that.getQuestionTypeClass()) {
      return false;
    }
    if (getQuestionHandlerClass() != that.getQuestionHandlerClass()) {
      return false;
    }
    return Objects.equals(getMetadata(), that.getMetadata());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getQuestionId(),
        getQuestionTypeClass(),
        getAnswerTypeClass(),
        getQuestionHandlerClass(),
        getOriginalResult(),
        getMetadata());
  }

  @Override
  public String toString() {
    return String.format(
        "Question %s %s %s %s %s %s",
        getQuestionId(),
        getQuestionTypeClass().getSimpleName(),
        getAnswerTypeClass().getSimpleName(),
        getQuestionHandlerClass().getSimpleName(),
        getOriginalResult(),
        getMetadata());
  }
}
