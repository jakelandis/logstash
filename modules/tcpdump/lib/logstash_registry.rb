LogStash::PLUGIN_REGISTRY.add(:modules, "tcpdump", LogStash::Modules::Scaffold.new("tcpdump", File.join(File.dirname(__FILE__), "..", "configuration")))
