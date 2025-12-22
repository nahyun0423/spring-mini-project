package com.example.kindergarten.application;

import com.example.kindergarten.domain.task.ChecklistItem;
import com.example.kindergarten.domain.task.ChecklistLog;
import com.example.kindergarten.domain.task.ChecklistAction;
import com.example.kindergarten.domain.user.User;
import com.example.kindergarten.infrastructure.ChecklistItemRepository;
import com.example.kindergarten.infrastructure.ChecklistLogRepository;
import com.example.kindergarten.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChecklistCompleteService {

    private final ChecklistItemRepository itemRepository;
    private final ChecklistLogRepository logRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChecklistItem complete(Long itemId, Long userId) {
        ChecklistItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("ChecklistItem 없음: " + itemId));

        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User 없음: " + userId));

        item.complete(actor);

        logRepository.save(new ChecklistLog(item, actor, ChecklistAction.COMPLETE));

        return item;
    }
}
