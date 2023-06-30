package org.kozak127.templates.restmysqlspring.apple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleRepository extends JpaRepository<Apple, String> {
}
