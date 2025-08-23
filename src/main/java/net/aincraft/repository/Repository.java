package net.aincraft.repository;

import net.aincraft.repository.Repository.RepositoryRecord;

public interface Repository<K, R extends RepositoryRecord<K>> {

  void save(R record);

  R load(K key);

  void delete(K key);

  interface RepositoryRecord<K> {
    K getKey();
  }
}
