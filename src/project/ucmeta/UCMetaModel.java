package project.ucmeta;

import java.util.List;
import java.util.ArrayList;

/**
 * UCMeta模型的核心数据结构
 */
public class UCMetaModel {
    
    /**
     * 用例类
     */
    public static class UseCase {
        private String name;
        private String description;
        private List<String> preconditions;
        private List<String> postconditions;
        private List<Sentence> mainFlow;
        private List<AlternativeFlow> alternativeFlows;
        private List<GlobalAlternativeFlow> globalAlternativeFlows;
        
        public UseCase(String name) {
            this.name = name;
            this.preconditions = new ArrayList<>();
            this.postconditions = new ArrayList<>();
            this.mainFlow = new ArrayList<>();
            this.alternativeFlows = new ArrayList<>();
            this.globalAlternativeFlows = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getPreconditions() { return preconditions; }
        public List<String> getPostconditions() { return postconditions; }
        public List<Sentence> getMainFlow() { return mainFlow; }
        public List<AlternativeFlow> getAlternativeFlows() { return alternativeFlows; }
        public List<GlobalAlternativeFlow> getGlobalAlternativeFlows() { return globalAlternativeFlows; }
    }
    
    /**
     * 备选流
     */
    public static class AlternativeFlow {
        private String id;
        private String condition;
        private List<Sentence> sentences;
        
        public AlternativeFlow(String id, String condition) {
            this.id = id;
            this.condition = condition;
            this.sentences = new ArrayList<>();
        }
        
        public String getId() { return id; }
        public String getCondition() { return condition; }
        public List<Sentence> getSentences() { return sentences; }
    }
    
    /**
     * 全局备选流
     */
    public static class GlobalAlternativeFlow {
        private String triggerEvent;
        private List<Sentence> sentences;
        
        public GlobalAlternativeFlow(String triggerEvent) {
            this.triggerEvent = triggerEvent;
            this.sentences = new ArrayList<>();
        }
        
        public String getTriggerEvent() { return triggerEvent; }
        public List<Sentence> getSentences() { return sentences; }
    }
    
    /**
     * 句子基类
     */
    public static abstract class Sentence {
        protected String id;
        protected String content;
        
        public Sentence(String id, String content) {
            this.id = id;
            this.content = content;
        }
        
        public String getId() { return id; }
        public String getContent() { return content; }
        public abstract SentenceType getType();
    }
    
    /**
     * 句子类型枚举
     */
    public enum SentenceType {
        SIMPLE,
        CONDITION_CHECK,
        CONDITIONAL,
        PARALLEL,
        ITERATIVE,
        INCLUDE,
        EXTEND,
        ABORT,
        RESUME_STEP
    }
    
    /**
     * 事务类型枚举
     */
    public enum TransactionType {
        INITIATION,
        RESPONSE_TO_PRIMARY_ACTOR,
        RESPONSE_TO_SECONDARY_ACTOR,
        INTERNAL_TRANSACTION
    }
    
    /**
     * 简单句
     */
    public static class SimpleSentence extends Sentence {
        private String actor;
        private String action;
        private String object;
        private TransactionType transactionType;
        
        public SimpleSentence(String id, String content, String actor, String action, String object, TransactionType transactionType) {
            super(id, content);
            this.actor = actor;
            this.action = action;
            this.object = object;
            this.transactionType = transactionType;
        }
        
        @Override
        public SentenceType getType() { return SentenceType.SIMPLE; }
        
        public String getActor() { return actor; }
        public String getAction() { return action; }
        public String getObject() { return object; }
        public TransactionType getTransactionType() { return transactionType; }
    }
    
    /**
     * 条件检查句
     */
    public static class ConditionCheckSentence extends Sentence {
        private String condition;
        private List<Sentence> alternativeFlow;
        
        public ConditionCheckSentence(String id, String content, String condition) {
            super(id, content);
            this.condition = condition;
            this.alternativeFlow = new ArrayList<>();
        }
        
        @Override
        public SentenceType getType() { return SentenceType.CONDITION_CHECK; }
        
        public String getCondition() { return condition; }
        public List<Sentence> getAlternativeFlow() { return alternativeFlow; }
    }
    
    /**
     * 条件句
     */
    public static class ConditionalSentence extends Sentence {
        private String condition;
        private List<Sentence> thenBranch;
        private List<Sentence> elseBranch;
        private List<ConditionalBranch> elseIfBranches;
        
        public ConditionalSentence(String id, String content, String condition) {
            super(id, content);
            this.condition = condition;
            this.thenBranch = new ArrayList<>();
            this.elseBranch = new ArrayList<>();
            this.elseIfBranches = new ArrayList<>();
        }
        
        @Override
        public SentenceType getType() { return SentenceType.CONDITIONAL; }
        
        public String getCondition() { return condition; }
        public List<Sentence> getThenBranch() { return thenBranch; }
        public List<Sentence> getElseBranch() { return elseBranch; }
        public List<ConditionalBranch> getElseIfBranches() { return elseIfBranches; }
        
        public static class ConditionalBranch {
            private String condition;
            private List<Sentence> sentences;
            
            public ConditionalBranch(String condition) {
                this.condition = condition;
                this.sentences = new ArrayList<>();
            }
            
            public String getCondition() { return condition; }
            public List<Sentence> getSentences() { return sentences; }
        }
    }
    
    /**
     * 并行句
     */
    public static class ParallelSentence extends Sentence {
        private List<List<Sentence>> parallelBranches;
        
        public ParallelSentence(String id, String content) {
            super(id, content);
            this.parallelBranches = new ArrayList<>();
        }
        
        @Override
        public SentenceType getType() { return SentenceType.PARALLEL; }
        
        public List<List<Sentence>> getParallelBranches() { return parallelBranches; }
    }
    
    /**
     * 迭代句
     */
    public static class IterativeSentence extends Sentence {
        private String condition;
        private List<Sentence> body;
        
        public IterativeSentence(String id, String content, String condition) {
            super(id, content);
            this.condition = condition;
            this.body = new ArrayList<>();
        }
        
        @Override
        public SentenceType getType() { return SentenceType.ITERATIVE; }
        
        public String getCondition() { return condition; }
        public List<Sentence> getBody() { return body; }
    }
    
    /**
     * 包含句
     */
    public static class IncludeSentence extends Sentence {
        private String includedUseCase;
        
        public IncludeSentence(String id, String content, String includedUseCase) {
            super(id, content);
            this.includedUseCase = includedUseCase;
        }
        
        @Override
        public SentenceType getType() { return SentenceType.INCLUDE; }
        
        public String getIncludedUseCase() { return includedUseCase; }
    }
    
    /**
     * 扩展句
     */
    public static class ExtendSentence extends Sentence {
        private String extendingUseCase;
        
        public ExtendSentence(String id, String content, String extendingUseCase) {
            super(id, content);
            this.extendingUseCase = extendingUseCase;
        }
        
        @Override
        public SentenceType getType() { return SentenceType.EXTEND; }
        
        public String getExtendingUseCase() { return extendingUseCase; }
    }
    
    /**
     * 中止句
     */
    public static class AbortSentence extends Sentence {
        public AbortSentence(String id, String content) {
            super(id, content);
        }
        
        @Override
        public SentenceType getType() { return SentenceType.ABORT; }
    }
    
    /**
     * 恢复步骤句
     */
    public static class ResumeStepSentence extends Sentence {
        private String targetStepId;
        
        public ResumeStepSentence(String id, String content, String targetStepId) {
            super(id, content);
            this.targetStepId = targetStepId;
        }
        
        @Override
        public SentenceType getType() { return SentenceType.RESUME_STEP; }
        
        public String getTargetStepId() { return targetStepId; }
    }
}
