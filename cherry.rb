def cmd(command)
  puts `#{command}`
end

args = ARGV
if args.size!=2
  puts "Usage: ruby cherry.rb <commit> [start-branch]"
else
  commit = ARGV[0]
  MIN_BRANCH = 0
  MAX_BRANCH = 7
  start_branch = ARGV[1].to_i
  end_branch = MAX_BRANCH
  if start_branch<MIN_BRANCH or start_branch>MAX_BRANCH
    puts "invalid start branch:#{start_branch}, should be between #{MIN_BRANCH} and #{MAX_BRANCH}"
  else
    puts "Cherry picking #{commit} into branchs #{start_branch}..#{end_branch}"
    (start_branch..end_branch).each do |t|
      cmd "git co #{t}"
      cmd "git cherry-pick #{commit}"
    end
  end
end
