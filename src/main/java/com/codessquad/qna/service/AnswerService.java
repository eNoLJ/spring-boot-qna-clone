package com.codessquad.qna.service;

import com.codessquad.qna.exception.AnswerNotFoundException;
import com.codessquad.qna.exception.IllegalUserAccessException;
import com.codessquad.qna.model.Answer;
import com.codessquad.qna.model.User;
import com.codessquad.qna.repository.AnswerRepository;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }

    public void save(Long id, Answer answer, User sessionUser) {
        answer.save(sessionUser, questionService.findById(id));
        this.answerRepository.save(answer);
    }

    public void update(Long id, Answer answer, User sessionUser) {
        Answer targetAnswer = verifyAnswer(id, sessionUser);
        targetAnswer.update(answer);
        this.answerRepository.save(targetAnswer);
    }

    public void delete(Long id, User sessionUser) {
        Answer targetAnswer = verifyAnswer(id, sessionUser);
        targetAnswer.delete();
        this.answerRepository.save(targetAnswer);
    }

    public Answer verifyAnswer(Long id, User sessionUser) {
        Answer targetAnswer = findById(id);
        if (!targetAnswer.matchWriter(sessionUser)) {
            throw new IllegalUserAccessException();
        }
        return targetAnswer;
    }

    public Answer findById(Long id) {
        Answer answer = this.answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);
        if (answer.isDeleted()) {
            throw new AnswerNotFoundException();
        }
        return answer;
    }

    public Long findQuestionId(Long answerId) {
        return findById(answerId).getQuestionId();
    }

}
