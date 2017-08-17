package org.logstash.instrument.witness;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link QueueWitness}
 */
public class QueueWitnessTest {

    private QueueWitness witness;

    @Before
    public void setup() {
        witness = new QueueWitness();
    }

    @Test
    public void testType() {
        witness.type("memory");
        assertThat(witness.snitch().type()).isEqualTo("memory");
    }

    @Test
    public void testQueueSizeInBytes(){
        witness.capacity().queueSizeInBytes(99);
        assertThat(witness.capacity().snitch().queueSizeInBytes()).isEqualTo(99);
    }

    @Test
    public void testPageCapacityInBytes(){
        witness.capacity().pageCapacityInBytes(98);
        assertThat(witness.capacity().snitch().pageCapacityInBytes()).isEqualTo(98);
    }

    @Test
    public void testMaxQueueSizeInBytes(){
        witness.capacity().maxQueueSizeInBytes(97);
        assertThat(witness.capacity().snitch().maxQueueSizeInBytes()).isEqualTo(97);
    }

    @Test
    public void testMaxUnreadEvents(){
        witness.capacity().maxUnreadEvents(96);
        assertThat(witness.capacity().snitch().maxUnreadEvents()).isEqualTo(96);
    }

    @Test
    public void testPath(){
        witness.data().path("/var/ls/q");
        assertThat(witness.data().snitch().path()).isEqualTo("/var/ls/q");
    }

    @Test
    public void testFreeSpace(){
        witness.data().freeSpaceInBytes(77);
        assertThat(witness.data().snitch().freeSpaceInBytes()).isEqualTo(77);
    }

    @Test
    public void testStorageType(){
        witness.data().storageType("ext4");
        assertThat(witness.data().snitch().storageType()).isEqualTo("ext4");
    }

    @Test
    public void testAsJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.writeValueAsString(witness)).isEqualTo(witness.asJson());
    }

    @Test
    public void testSerializeEmpty() throws Exception {
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":null}}");
    }

    @Test
    public void testSerializeMemoryType() throws Exception {
        witness.type("memory");
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"memory\"}}");
    }

    @Test
    public void testSerializePersistedType() throws Exception {
        witness.type("persisted");
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeQueueSize() throws Exception {
        witness.type("persisted");
        witness.capacity().queueSizeInBytes(88);
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":88,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeQueuePageCapacity() throws Exception {
        witness.type("persisted");
        witness.capacity().pageCapacityInBytes(87);
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":87,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeMaxQueueSize() throws Exception {
        witness.type("persisted");
        witness.capacity().maxUnreadEvents(86);
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":86},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeMaxUnreadEvents() throws Exception {
        witness.type("persisted");
        witness.capacity().maxUnreadEvents(85);
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":85},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializePath() throws Exception{
        witness.type("persisted");
        witness.data().path("/var/ls/q2");
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":\"/var/ls/q2\",\"free_space_in_bytes\":0,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeFreeSpace() throws Exception{
        witness.type("persisted");
        witness.data().freeSpaceInBytes(66);
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":null,\"free_space_in_bytes\":66,\"storage_type\":null}}}");
    }

    @Test
    public void testSerializeStorageType() throws Exception{
        witness.type("persisted");
        witness.data().storageType("xfs");
        String json = witness.asJson();
        assertThat(json).isEqualTo("{\"queue\":{\"type\":\"persisted\",\"capacity\":{\"queue_size_in_bytes\":0,\"page_capacity_in_bytes\":0,\"max_queue_size_in_bytes\":0," +
                "\"max_unread_events\":0},\"data\":{\"path\":null,\"free_space_in_bytes\":0,\"storage_type\":\"xfs\"}}}");
    }
}